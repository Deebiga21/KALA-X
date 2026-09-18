import re

def extract_information(text: str) -> dict:
    # Deterministic fallback
    result = {}
    
    if "bamboo" in text.lower():
        result["material"] = "Bamboo"
    elif "wood" in text.lower():
        result["material"] = "Wood"
    
    # Extract cost
    cost_match = re.search(r'(\d+)\s*rupees', text.lower())
    if cost_match:
        result["raw_material_cost"] = float(cost_match.group(1))
        
    return result
