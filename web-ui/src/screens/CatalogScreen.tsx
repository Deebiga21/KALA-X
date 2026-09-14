import React from 'react';
import { motion } from 'framer-motion';
import { useStore } from '../store/useStore';
import { Package } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function CatalogScreen() {
  const products = useStore((state) => state.products);
  const navigate = useNavigate();

  const published = products.filter(p => p.status === 'Published');
  const drafts = products.filter(p => p.status === 'Draft');

  return (
    <div className="h-full w-full overflow-y-auto no-scrollbar pb-32 relative z-10 px-6 pt-12">
      <h1 className="text-3xl font-bold text-white mb-2">My Catalog</h1>
      <p className="text-gray-400 text-sm mb-6">Manage your products and listings.</p>
      
      <div className="glass-card p-5 grid grid-cols-3 divide-x divide-white/10 mb-8">
        <div className="flex flex-col items-center justify-center">
          <span className="text-xl font-bold text-white">{products.length}</span>
          <span className="text-[10px] text-gray-400">Total Products</span>
        </div>
        <div className="flex flex-col items-center justify-center">
          <span className="text-xl font-bold text-cyan-400">{published.length}</span>
          <span className="text-[10px] text-gray-400">Published</span>
        </div>
        <div className="flex flex-col items-center justify-center">
          <span className="text-xl font-bold text-violet-400">{drafts.length}</span>
          <span className="text-[10px] text-gray-400">Drafts</span>
        </div>
      </div>

      <div className="space-y-4">
        {products.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-10 glass-card">
            <Package size={48} className="text-gray-600 mb-4" />
            <p className="text-gray-400 mb-4">No products in your catalog yet.</p>
            <button 
              onClick={() => navigate('/create')}
              className="bg-cyan-500 text-black px-6 py-2 rounded-full font-medium"
            >
              Create Product
            </button>
          </div>
        ) : (
          products.map(product => (
            <motion.div 
              key={product.id}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              className="glass-card p-4 flex gap-4"
            >
              <img src={product.enhancedImage || product.image} alt={product.name} className="w-20 h-20 rounded-xl object-cover" />
              <div className="flex-1 flex flex-col justify-center">
                <h3 className="text-white font-semibold">{product.name}</h3>
                <div className="flex justify-between items-center mt-1">
                  <span className="text-cyan-400 font-bold">₹{product.recommendedPrice || 0}</span>
                  <span className={`text-[10px] px-2 py-1 rounded-full ${product.status === 'Published' ? 'bg-cyan-500/20 text-cyan-400' : 'bg-yellow-500/20 text-yellow-400'}`}>
                    {product.status}
                  </span>
                </div>
                <div className="mt-2 text-xs text-gray-400 flex justify-between">
                  <span>Score: <span className="text-violet-400 font-bold">{product.commerceScore}/100</span></span>
                </div>
              </div>
            </motion.div>
          ))
        )}
      </div>
    </div>
  );
}
