package eu.luftiger.mdbot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MedicationHandler {

    private List<Medication> medications;

    public void loadMedications() throws IOException {
        File medicationFile = new File("medications.json");
        if(!medicationFile.exists()){
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("medications.json");
            Files.copy(inputStream, Paths.get("medications.json"));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        medications = Arrays.stream(objectMapper.readValue(medicationFile, Medication[].class)).toList();

        for (Medication medication : medications) {
            System.out.println(medication.name());
        }


        getAreas();
    }

    public List<String> getAreas(){
        List<String> areas = new ArrayList<>();
        for (Medication medication : medications) {
            for (String s : medication.symptom_location()) {
                if(!areas.contains(s))
                    areas.add(s);
            }
        }

        return areas;
    }

    public List<String> getSymptomsOfArea(String area){
        List<String> symptoms = new ArrayList<>();
        for (Medication medication : medications) {
            if(medication.symptom_location().contains(area)){
                for (String symptom : medication.symptoms()) {
                    if(!symptoms.contains(symptom))
                        symptoms.add(symptom);
                }
            }
        }

        return symptoms;
    }

    public List<Medication> getMedications() {
        return medications;
    }
}
