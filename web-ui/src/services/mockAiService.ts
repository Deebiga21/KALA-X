export const mockAiService = {
  enhanceImage: async (imageFile: File | string): Promise<string> => {
    // Simulate network and processing delay
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    // In a real app, this would return the enhanced image URL from the server.
    // For the demo, we return a polished, enhanced version of a bamboo basket.
    return "https://images.unsplash.com/photo-1606760227091-3dd870d97f1d?q=80&w=800&auto=format&fit=crop"; 
  },

  transcribeAndTranslate: async (audioBlob: Blob | null, language: string) => {
    // Simulate delay
    await new Promise(resolve => setTimeout(resolve, 2500));
    
    return {
      originalText: "இந்த கூடை மூங்கில் கொண்டு கையால் செய்யப்பட்டது. இது மிகவும் அழகாகவும் உறுதியாகவும் இருக்கும்.",
      englishTranslation: "This basket is handmade using bamboo. It is very beautiful and sturdy."
    };
  },

  generateCatalog: async (translation: string) => {
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    return {
      name: "Handmade Bamboo Basket",
      category: "Home & Lifestyle",
      material: "Natural Bamboo",
      description: "A beautifully handcrafted bamboo basket made using traditional weaving techniques. Eco-friendly, sturdy, and perfect for storage or decoration.",
      keywords: ["bamboo basket", "handmade", "eco-friendly", "Indian handicraft", "storage"]
    };
  },

  calculatePrice: async (costs: { raw: number, labour: number, packaging: number, other: number }) => {
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    const totalCost = costs.raw + costs.labour + costs.packaging + costs.other;
    // Add a ~30-40% margin
    const recommendedPrice = Math.round(totalCost * 1.35);
    
    return {
      totalCost,
      recommendedPrice,
      marketMin: recommendedPrice - 50,
      marketMax: recommendedPrice + 150,
      confidence: 87
    };
  },
  
  calculateCommerceScore: (product: any) => {
    let score = 0;
    if (product.enhancedImage) score += 30;
    if (product.name && product.name.length > 5) score += 10;
    if (product.description && product.description.length > 20) score += 15;
    if (product.recommendedPrice > 0) score += 20;
    if (product.category) score += 10;
    if (product.keywords && product.keywords.length > 2) score += 5;
    if (product.dimensions && product.dimensions.length > 2) score += 10;
    
    return Math.min(100, score);
  }
};
