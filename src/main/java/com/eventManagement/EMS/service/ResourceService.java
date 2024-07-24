package com.eventManagement.EMS.service;


import com.eventManagement.EMS.DTO.ResourceDTO;
import com.eventManagement.EMS.models.Resource;
import com.eventManagement.EMS.repository.EventRepository;
import com.eventManagement.EMS.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    EventRepository eventRepository;

    public ResponseEntity<String> addResource(ResourceDTO resourceDTO){
        Resource resource = new Resource();
        resource.setName(resourceDTO.getName());
        resource.setType(resourceDTO.getType());
        resource.setDescription(resourceDTO.getDescription());
        resource.setAvailability(resourceDTO.isAvailability());
        resourceRepository.save(resource);
        return new ResponseEntity<>("Resource has been saved", HttpStatus.CREATED);
    }

    public ResponseEntity<String> editResource(Long resourceId, Resource updatedResource){
        Optional<Resource> resourceOptional = resourceRepository.findById(resourceId);
        if(resourceOptional.isEmpty()){
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        }
        Resource existingResource = resourceOptional.get();
        existingResource.setAvailability(updatedResource.getAvailability() ? updatedResource.getAvailability() : existingResource.getAvailability());
        existingResource.setDescription(updatedResource.getDescription() != null ? updatedResource.getDescription() : existingResource.getDescription());
        existingResource.setName(updatedResource.getName() != null ? updatedResource.getName() : existingResource.getName());
        existingResource.setType(updatedResource.getType() != null ? updatedResource.getType() : existingResource.getType());
        resourceRepository.save(existingResource);
        return new ResponseEntity<>("Resource has been updated", HttpStatus.OK);
    }
    public ResponseEntity<List<ResourceDTO>> getAllResource(){
        List<Resource> resources = resourceRepository.findAll();
        List<ResourceDTO> resourceDTOList = new ArrayList<>();
        for(Resource resource : resources){
            ResourceDTO resourceDTO = new ResourceDTO();
            resourceDTO.setName(resource.getName());
            resourceDTO.setId(resource.getId());
            resourceDTO.setDescription(resource.getDescription());
            resourceDTO.setType(resource.getType());
            resourceDTO.setAvailability(resource.getAvailability());
            if(resource.getEventResource() != null){
                resourceDTO.setEvent(resource.getEventResource().getName());
            }
            resourceDTOList.add(resourceDTO);
        }
        return new ResponseEntity<>(resourceDTOList, HttpStatus.OK);
    }
    public ResponseEntity<String> deleteResource(Long resourceId){
        Optional<Resource> resourceOptional = resourceRepository.findById(resourceId);
        if(resourceOptional.isEmpty()){
            return new ResponseEntity<>("Resource not found", HttpStatus. NOT_FOUND);
        }
        Resource resource = resourceOptional.get();
        resourceRepository.delete(resource);
        return new ResponseEntity<>("Resource has been deleted", HttpStatus.OK);
    }
}
