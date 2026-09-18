def process_image(filepath: str, description: str) -> dict:
    # Dummy deterministic fallback based on description
    material = "Unknown"
    if "bamboo" in description.lower():
        material = "Bamboo"
    elif "wood" in description.lower():
        material = "Wood"
    elif "clay" in description.lower():
        material = "Clay"
        
    return {"material": material}
