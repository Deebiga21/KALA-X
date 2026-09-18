def calculate_price(raw: float, labor: float, packaging: float, margin_pct: float) -> float:
    base_cost = raw + labor + packaging
    margin = base_cost * (margin_pct / 100.0)
    return base_cost + margin
