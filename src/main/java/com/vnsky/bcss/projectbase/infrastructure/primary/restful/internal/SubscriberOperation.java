package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Subscriber internal apis")
@RequestMapping("${application.path.base.private}/subscriber")
public interface SubscriberOperation {
    @GetMapping("/{isdn}")
    ResponseEntity<Object> findByIsdn(@PathVariable Long isdn);

    @GetMapping("/file")
    ResponseEntity<Object> downloadFile( @RequestParam String file);
}
