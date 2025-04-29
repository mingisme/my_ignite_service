package com.swang.myservice;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MyService {

    private final Ignite ignite;

    public MyService(Ignite ignite) {
        this.ignite = ignite;
    }

    @PostMapping("/set")
    public MyEntry setValue(@RequestBody MyEntry entry) {
        IgniteCache<String, String> cache = ignite.getOrCreateCache("testCache");
        cache.put(entry.key,entry.value);
        return entry;
    }

    @GetMapping("/get")
    public MyEntry getValue(@RequestParam String key){
        IgniteCache<String, String> cache = ignite.getOrCreateCache("testCache");
        String value = cache.get(key);
        MyEntry myEntry = new MyEntry();
        myEntry.key = key;
        myEntry.value = value;
        return myEntry;
    }

}
