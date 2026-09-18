def calculate_readiness(product) -> float:
    fields_to_check = [
        product.name,
        product.description,
        product.image_url,
        product.final_price,
        product.catalog_title,
        product.catalog_description
    ]
    
    filled = sum(1 for field in fields_to_check if field is not None and field != "")
    score = (filled / len(fields_to_check)) * 100
    return round(score, 2)
