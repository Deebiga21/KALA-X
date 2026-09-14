import { create } from 'zustand';

export interface Product {
  id: string;
  name: string;
  category: string;
  material: string;
  image: string;
  enhancedImage?: string;
  description: string;
  originalLanguage?: string;
  transcription?: string;
  translation?: string;
  keywords: string[];
  rawMaterialCost?: number;
  labourCost?: number;
  packagingCost?: number;
  otherCost?: number;
  totalCost?: number;
  marketMin?: number;
  marketMax?: number;
  recommendedPrice?: number;
  pricingConfidence?: number;
  commerceScore?: number;
  dimensions?: string;
  status: 'Draft' | 'Published';
  createdAt: string;
}

interface AppState {
  products: Product[];
  addProduct: (product: Product) => void;
  updateProduct: (id: string, updates: Partial<Product>) => void;
  deleteProduct: (id: string) => void;
  
  // For the creation flow
  draftProduct: Partial<Product> | null;
  setDraftProduct: (updates: Partial<Product>) => void;
  clearDraftProduct: () => void;
}

export const useStore = create<AppState>((set) => ({
  products: [
    // Pre-populate with some demo data so the catalog isn't empty initially, or start empty. Let's start with empty so they see their created product.
  ],
  addProduct: (product) => set((state) => ({ products: [...state.products, product] })),
  updateProduct: (id, updates) => set((state) => ({
    products: state.products.map(p => p.id === id ? { ...p, ...updates } : p)
  })),
  deleteProduct: (id) => set((state) => ({
    products: state.products.filter(p => p.id !== id)
  })),

  draftProduct: null,
  setDraftProduct: (updates) => set((state) => ({
    draftProduct: state.draftProduct ? { ...state.draftProduct, ...updates } : updates
  })),
  clearDraftProduct: () => set({ draftProduct: null })
}));
