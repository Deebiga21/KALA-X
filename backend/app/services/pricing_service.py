from app.models.product import Product

class PricingService:
    def calculate_price(self, product: Product, market_min: float = 799.0, market_max: float = 899.0):
        total_cost = product.raw_material_cost + product.labour_cost + product.packaging_cost + product.other_cost
        base_margin = 0.30
        
        cost_based_price = total_cost * (1 + base_margin)
        
        # Simple recommendation logic for mock
        if cost_based_price < market_min:
            recommended = (market_min + cost_based_price) / 2
            confidence = 87
        elif cost_based_price > market_max:
            recommended = cost_based_price
            confidence = 60 # Not competitive
        else:
            recommended = cost_based_price
            confidence = 95
            
        return {
            "total_cost": total_cost,
            "market_min": market_min,
            "market_max": market_max,
            "recommended_price": round(recommended, 2),
            "pricing_confidence": confidence
        }
