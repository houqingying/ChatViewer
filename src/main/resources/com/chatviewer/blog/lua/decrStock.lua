-- 检查确认key确实存在
if (redis.call('exists', KEYS[1]) == 1)
then
    if (redis.call('exists', KEYS[2]) == 1 and redis.call('sismember', KEYS[2], ARGV[1]) == 1)
    then
        -- 如果用户已经购买过，不能重复购买
        return -1;
    end
    -- 获取当前库存
    local stock = redis.call('hget', KEYS[1], "stock") + 0
    if (stock > 0)
    then
        -- 减库存并记录用户已购买
        redis.call('hset', KEYS[1], "stock", tostring(stock - 1))
        redis.call('sadd', KEYS[2], ARGV[1])
        -- 如果脚本返回值 > 0，证明减库存成功，否则减库存失败
        return stock
    end
    return 0
else
    return -1
end