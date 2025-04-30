package com.swang.myservice;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.web.bind.annotation.*;

import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class MyService {

    public static final String MY_CACHE = "myCache";
    private final Ignite ignite;

    public MyService(Ignite ignite) {
        this.ignite = ignite;
    }

    @PostMapping("/set")
    public MyEntry setValue(@RequestBody MyEntry entry) {
        IgniteCache<String, String> cache = ignite.getOrCreateCache(MY_CACHE);
        ExpiryPolicy slidingExpiry = new AccessedExpiryPolicy(new Duration(TimeUnit.HOURS, 1));
        cache.withExpiryPolicy(slidingExpiry);
        cache.put(entry.key,entry.value);
        return entry;
    }

    @GetMapping("/get")
    public MyEntry getValue(@RequestParam String key){
        IgniteCache<String, String> cache = ignite.getOrCreateCache(MY_CACHE);
        String value = cache.get(key);
        MyEntry myEntry = new MyEntry();
        myEntry.key = key;
        myEntry.value = value;
        return myEntry;
    }

}
