package com.example.domain.products.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UploadResult {
	
	private List<String> errors = new ArrayList<>();
    private String filename; 

    public UploadResult(List<String> errors,String filename) {
        this.errors = errors;
        this.filename = filename;
    }
    
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}
