def generate_catalog(product_name: str, description: str, material: str) -> dict:
    mat = material if material else "High Quality Material"
    title = f"Premium {mat} {product_name}"
    cat_desc = f"Discover our handcrafted {product_name}, made from {mat}. {description}"
    seo_tags = f"{product_name.lower()}, {mat.lower()}, handcrafted, artisan"
    
    return {
        "catalog_title": title,
        "catalog_description": cat_desc,
        "catalog_seo_tags": seo_tags
    }
