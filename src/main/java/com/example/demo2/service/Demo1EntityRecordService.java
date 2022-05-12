package com.example.demo2.service;

/**
 * @author wlei3
 */
//@Slf4j
//@Service
//public class Demo1EntityRecordService extends ServiceImpl<Demo1EntityRecordMapper, Demo1Entity> {
//
//    public Demo1Entity selectByName(String name) {
//        return getOne(Wrappers.<Demo1Entity>lambdaQuery().eq(Demo1Entity::getName, name));
//    }
//
//    public void insert(String name) {
//        // 模拟耗时操作
//        try {
//            TimeUnit.SECONDS.sleep(2);
//        } catch (InterruptedException e) {
//            log.warn("Interrupted!", e);
//            Thread.currentThread().interrupt();
//        }
//
//        Demo1Entity entity = new Demo1Entity();
//        entity.setName(name);
//        save(entity);
//    }
//
//    @Autowired
//    private RedissonClient redissonClient;
//
//    public void selectAndInsert(String name) {
//        final RLock lock = redissonClient.getLock("anyLock");
//        lock.lock();
//        try {
//            Demo1Entity demo1Entity = selectByName(name);
//            if (ObjectUtil.isNotEmpty(demo1Entity)) {
//                return;
//            }
//            insert(name);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            lock.unlock();
//        }
//    }
//}
