from app.models.product import Product

class CommerceScoreService:
    def calculate_score(self, product: Product):
        breakdown = {
            "image": 0,
            "description": 0,
            "pricing": 0,
            "category": 0,
            "keywords": 0,
            "details": 0
        }
        missing = []
        
        if product.original_image or product.enhanced_image:
            breakdown["image"] = 20
        else:
            missing.append("image")
            
        if product.description:
            breakdown["description"] = 20
        else:
            missing.append("description")
            
        if product.recommended_price and product.recommended_price > 0:
            breakdown["pricing"] = 20
        else:
            missing.append("price")
            
        if product.category:
            breakdown["category"] = 15
        else:
            missing.append("category")
            
        if product.keywords:
            breakdown["keywords"] = 10
        else:
            missing.append("keywords")
            
        details_score = 0
        if product.dimensions:
            details_score += 7.5
        else:
            missing.append("dimensions")
            
        if product.weight:
            details_score += 7.5
        else:
            missing.append("weight")
            
        breakdown["details"] = int(details_score)
        
        total = sum(breakdown.values())
        
        status = "Ready" if total >= 80 else "Needs Improvement"
        if total == 100:
            status = "Excellent"
            
        return {
            "score": total,
            "status": status,
            "breakdown": breakdown,
            "missing_fields": missing
        }
