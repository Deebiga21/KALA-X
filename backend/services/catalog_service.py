import google.generativeai as genai
import json
import os

# TODO: For the hackathon, paste your Gemini API key here!
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "YOUR_API_KEY_HERE")

def generate_catalog(product_name: str, description: str, material: str) -> dict:
    if not GEMINI_API_KEY or GEMINI_API_KEY == "YOUR_API_KEY_HERE":
        # Fallback to dummy template if no API key is set yet
        mat = material if material else "High Quality Material"
        title = f"Premium {mat} {product_name}" if product_name else f"Premium {mat} Product"
        cat_desc = f"Discover our handcrafted product, made from {mat}. {description}"
        seo_tags = f"{str(product_name).lower()}, {mat.lower()}, handcrafted, artisan, small business"
        return {
            "catalog_title": title.strip(),
            "catalog_description": cat_desc.strip(),
            "catalog_seo_tags": seo_tags
        }
        
    try:
        genai.configure(api_key=GEMINI_API_KEY)
        model = genai.GenerativeModel('gemini-1.5-flash')
        
        prompt = f"""
        You are an expert e-commerce copywriter for a small business.
        Create a professional, SEO-optimized product listing based on this transcribed description.
        Product Name: {product_name}
        Material: {material}
        Spoken Description: {description}
        
        Return ONLY a JSON object with exactly these 3 keys:
        "catalog_title": A catchy, SEO friendly title (max 60 chars)
        "catalog_description": A beautifully written, persuasive description paragraph ready for Instagram or Amazon
        "catalog_seo_tags": A comma separated list of 5-10 highly searchable SEO keywords
        """
        
        response = model.generate_content(prompt)
        text = response.text.strip()
        
        # Clean markdown formatting if present
        if text.startswith("```json"):
            text = text[7:-3].strip()
        elif text.startswith("```"):
            text = text[3:-3].strip()
            
        data = json.loads(text)
        return {
            "catalog_title": data.get("catalog_title", f"Premium {product_name}"),
            "catalog_description": data.get("catalog_description", description),
            "catalog_seo_tags": data.get("catalog_seo_tags", "handmade, artisan")
        }
    except Exception as e:
        print("LLM Error:", e)
        # Fallback if API fails
        return {
            "catalog_title": f"Premium {product_name}",
            "catalog_description": description,
            "catalog_seo_tags": "handmade, artisan"
        }
