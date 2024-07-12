package com.eventManagement.EMS.service;


import com.eventManagement.EMS.models.Resource;
import com.eventManagement.EMS.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    @Autowired
    ResourceRepository resourceRepository;


    public ResponseEntity<String> addResource(Resource resource){
        resourceRepository.save(resource);
        return new ResponseEntity<>("Resource has been saved", HttpStatus.CREATED);
    }

    public ResponseEntity<String> editResource(Long resourceId, Resource updatedResource){
        Optional<Resource> resourceOptional = resourceRepository.findById(resourceId);
        if(resourceOptional.isEmpty()){
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        }
        Resource existingResource = resourceOptional.get();
        existingResource.setAvailability(updatedResource.getAvailability() != null ? updatedResource.getAvailability() : existingResource.getAvailability());
        existingResource.setDescription(updatedResource.getDescription() != null ? updatedResource.getDescription() : existingResource.getDescription());
        existingResource.setName(updatedResource.getName() != null ? updatedResource.getName() : existingResource.getName());
        existingResource.setType(updatedResource.getType() != null ? updatedResource.getType() : existingResource.getType());
        return new ResponseEntity<>("Resource has been updated", HttpStatus.OK);
    }
    public ResponseEntity<List<Resource>> getAllResource(){
        List<Resource> resources = resourceRepository.findAll();
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
    public ResponseEntity<String> deleteResource(Long resourceId){
        Optional<Resource> resourceOptional = resourceRepository.findById(resourceId);
        if(resourceOptional.isEmpty()){
            return new ResponseEntity<>("Resource not found", HttpStatus. NOT_FOUND);
        }
        Resource resource = resourceOptional.get();
        resourceRepository.delete(resource);
        return new ResponseEntity<>("Resource has been deleted", HttpStatus.NO_CONTENT);
    }
}
