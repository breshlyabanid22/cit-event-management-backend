package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.models.Resource;
import com.eventManagement.EMS.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    //Only admin user can access this endpoints
    @Autowired
    ResourceService resourceService;

    @PostMapping("/add")
    public ResponseEntity<String> addResource(@RequestBody Resource resource){
        return resourceService.addResource(resource);
    }

    @GetMapping("/all") //Fetches all resources
    public ResponseEntity<List<Resource>> getAllResources(){
        return resourceService.getAllResource();
    }

    @PutMapping("/edit/{resourceId}")
    public ResponseEntity<String> editResource(Long resourceId, @RequestBody Resource updatedResource){
        return resourceService.editResource(resourceId, updatedResource);
    }
    @DeleteMapping("/delete/{resourceId}")
    public ResponseEntity<String> deleteResource(Long resourceId){
        return resourceService.deleteResource(resourceId);
    }

}
