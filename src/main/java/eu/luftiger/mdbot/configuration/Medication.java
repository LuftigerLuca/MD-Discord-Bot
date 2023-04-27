package eu.luftiger.mdbot.configuration;

import java.util.List;

public record Medication(String name, List<String> symptoms, List<String> symptom_location, String dose, List<String> administer, String description, String entitled) {
}
