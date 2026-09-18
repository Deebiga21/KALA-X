def process_image(filepath: str, description: str, labels: str = "") -> dict:
    # Dummy deterministic fallback based on description and labels
    material = "Unknown"
    text_to_check = (description + " " + labels).lower()
    if "bamboo" in text_to_check:
        material = "Bamboo"
    elif "wood" in text_to_check:
        material = "Wood"
    elif "clay" in text_to_check:
        material = "Clay"
        
    return {"material": material}
