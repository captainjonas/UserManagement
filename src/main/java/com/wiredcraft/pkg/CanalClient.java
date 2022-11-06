package com.wiredcraft.pkg;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Eric Yao
 * @date 2022-11-06
 */
@Configuration
@Slf4j
public class CanalClient {

    @Value("${canal.host}")
    private String canalHost;

    @Value("${canal.port}")
    private Integer canalPort;

    @Value("${canal.username}")
    private String canalUsername;

    @Value("${canal.password}")
    private String canalPassword;

    @Value("${canal.topic}")
    private String canalTopic;

    @Value("${spring.redis.userKey}")
    private String userKey;

    @Resource
    private RedisTemplate redisTemplate;

    private static final int BATCH_SIZE = 1000;



    @Bean
    public void canalSync(){
        CanalConnector connector= CanalConnectors.newSingleConnector(new InetSocketAddress(canalHost,canalPort),
                canalTopic, canalUsername, canalPassword);
        connector.connect();
        int emptyCount = 0;
        try {
            connector.connect();
            // get binlog from datatbase test
            connector.subscribe("test\\..*");
            connector.rollback();
            int totalEmptyCount = 120;
            while (emptyCount < totalEmptyCount) {
                Message message = connector.getWithoutAck(BATCH_SIZE); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                } else {
                    emptyCount = 0;
                    printEntry(message.getEntries());
                }
                //confirm to commit
                connector.ack(batchId);
            }

            log.info("empty too many times, exit");
        } finally {
            connector.disconnect();
        }
    }

    private void printEntry(List<Entry> entrys) {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            EventType eventType = rowChage.getEventType();
            log.info(String.format("================&gt; binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                    // sync deleting to redis
                    delete(rowData.getBeforeColumnsList());
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                    // sync inserting or updating to redis
                    insertOrUpdate(rowData.getAfterColumnsList());
                } else {
                    System.out.println("-------&gt; before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("-------&gt; after");
                    printColumn(rowData.getAfterColumnsList());
                    // sync to redis
                    insertOrUpdate(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    /**
     * when monitoring mysql inserting or updating, sync it to redis
     * @param columns
     */
    private void insertOrUpdate (List<Column> columns) {
        if (columns.size() > 0) {
            JSONObject json = new JSONObject();
            for (Column column : columns) {
                json.put(column.getName(), column.getValue());
            }
            redisTemplate.opsForHash().put(userKey,columns.get(0).getValue(),json.toJSONString());
        }
    }

    /**
     * when monitoring mysql deleting, sync it to reds
     * @param columns
     */
    private void delete (List<Column> columns) {
        if (columns.size() > 0) {
            redisTemplate.opsForHash().delete(userKey, columns.get(0).getValue());
        }

    }


}
