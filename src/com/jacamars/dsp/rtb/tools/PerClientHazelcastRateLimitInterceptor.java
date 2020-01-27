package com.jacamars.dsp.rtb.tools;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.hazelcast.core.HazelcastInstance;

import com.hazelcast.core.IMap;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.hazelcast.Hazelcast;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A cluster aware singleton class for rate limiting. Current implementation has no way to
 * delete an api key. Once a rate is established, it is fixed.
 */
public enum PerClientHazelcastRateLimitInterceptor  {

    INSTANCE;

    private ProxyManager<String> buckets;
    private static IMap<String,Pair<Long,Duration>> capacities;

    /**
     * Create the instance and initialize the proxy manager for the IMap.
     * @param hzInstance HazelcastInstance. The instance we will use to hazel these limiters.
     * @return PerClientHazelcastRateLimitInterceptor.
     */
    public static PerClientHazelcastRateLimitInterceptor getInstance(HazelcastInstance hzInstance) {
        if (INSTANCE.buckets == null) {
            INSTANCE.buckets = Bucket4j.extension(Hazelcast.class)
                .proxyManagerForMap(hzInstance.getMap("per-client-bucket-map"));
        	capacities = hzInstance.getMap("capacities");
        }
       return  INSTANCE;

    }

    /**
     * Subsequent instance retriever after initialization.
     * @return PerClientHazelcastRateLimitInterceptor.
     */
    public static PerClientHazelcastRateLimitInterceptor getInstance() {
        return  INSTANCE;
    }


    /**
     * Register a key to be rate limited capacity amount per minute count.
     * @param key String. The key to limit.
     * @param capacity long. The capacity, eg "5"
     * @param count long. Rate limit in minutes, e.g. 1.
     */
    public void register(String key, long capacity, long count) {

        register(key,"MINUTES", capacity, count);
    }

    /**
     * Register a key to be rate limited capacity amount per "type" count.
     * @param key String. The key to limit.
     * @param type String. The UCASE duration type, e.g. "MINUTES".
     * @param capacity long. The capacity, eg "5"
     * @param count long. Rate limit in minutes, e.g. 1.
     */
    public void register(String key,  String type, long capacity, long count) {
        Duration d = null;
        switch(type) {
            case "MILLISECONDS":
                d = Duration.ofMillis(count);
                break;
            case "SECONDS":
                d = Duration.ofSeconds(count);
                break;
            case "MINUTES":
                d = Duration.ofMinutes(count);
                break;
            case "HOURS":
                d = Duration.ofHours(count);
                break;
            case "DAYS":
                d = Duration.ofDays(count);
                break;
            default:
                d = Duration.ofMinutes(count);
        }
        Pair<Long,Duration> p = Pair.of(capacity,d);
        capacities.put(key,p);
    }

    /**
     * Can we consume the requested value
     * @param key String. The key to check for blockage.
     * @param value long. Tokens to consume.
     * @return boolean True if it is blocked, otherwise returns false.
     */
    public boolean canCosume(String key, long value) {
    	Bucket requestBucket = this.buckets.getProxy(key, getConfigSupplier(key));
        boolean t = requestBucket.estimateAbilityToConsume(1).canBeConsumed();
        return t;
    }

    /**
     * Consume the indicated tokens.
     * Returns the number of tokens remaining if > 0. If < 0 it is number of milliseconds left before unblocking.
     * @param key String. The key to check.
     * @param value long. The number of tokens to deduct.
     * @return long value, positive is number of tokens left before blocking, and negative number of milliseconds before unblocked.
     */
    public long consume(String key, long value) {
        Bucket requestBucket = this.buckets.getProxy(key, getConfigSupplier(key));
        ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(value);
        if (probe.isConsumed()) {
            return probe.getRemainingTokens();
        }

        return  -TimeUnit.NANOSECONDS.toMillis(probe.getNanosToWaitForRefill());
    }

    /**
     * Returns the bucket for the key - will make a distributed bucket if it is not configured, otherwise will
     * return an already configured bucket.
     * @param key String. The key of the bucket.
     * @return the bucket needed.
     */
    private static Supplier<BucketConfiguration> getConfigSupplier(String key) {
        return () -> {
            var p = capacities.get(key);

            return Bucket4j.configurationBuilder()
                    .addLimit(Bandwidth.classic(p.getLeft(), Refill.intervally(p.getLeft(), p.getRight())))
                    .build();

        };
    }
}