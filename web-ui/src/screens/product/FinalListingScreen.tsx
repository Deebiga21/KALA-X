import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ArrowLeft, CheckCircle, Globe, Share2, Eye } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useStore, type Product } from '../../store/useStore';
import { mockAiService } from '../../services/mockAiService';

export default function FinalListingScreen() {
  const navigate = useNavigate();
  const draftProduct = useStore(state => state.draftProduct);
  const clearDraftProduct = useStore(state => state.clearDraftProduct);
  const addProduct = useStore(state => state.addProduct);
  
  const [isPublishing, setIsPublishing] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);

  const handlePublish = async () => {
    setIsPublishing(true);
    
    // Simulate network delay
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    const score = mockAiService.calculateCommerceScore(draftProduct);
    
    const newProduct: Product = {
      id: Math.random().toString(36).substr(2, 9),
      name: draftProduct?.name || 'Untitled Product',
      category: draftProduct?.category || 'Uncategorized',
      material: draftProduct?.material || 'Unknown',
      image: draftProduct?.image || '',
      enhancedImage: draftProduct?.enhancedImage,
      description: draftProduct?.description || '',
      originalLanguage: draftProduct?.originalLanguage,
      transcription: draftProduct?.transcription,
      translation: draftProduct?.translation,
      keywords: draftProduct?.keywords || [],
      rawMaterialCost: draftProduct?.rawMaterialCost,
      labourCost: draftProduct?.labourCost,
      packagingCost: draftProduct?.packagingCost,
      otherCost: draftProduct?.otherCost,
      totalCost: draftProduct?.totalCost,
      marketMin: draftProduct?.marketMin,
      marketMax: draftProduct?.marketMax,
      recommendedPrice: draftProduct?.recommendedPrice,
      pricingConfidence: draftProduct?.pricingConfidence,
      commerceScore: score,
      dimensions: draftProduct?.dimensions,
      status: 'Published',
      createdAt: new Date().toISOString()
    };
    
    addProduct(newProduct);
    clearDraftProduct();
    
    setIsPublishing(false);
    setShowSuccess(true);
    
    setTimeout(() => {
      navigate('/catalog');
    }, 2000);
  };

  const handleSaveDraft = () => {
    const score = mockAiService.calculateCommerceScore(draftProduct);
    
    const newProduct: Product = {
      id: Math.random().toString(36).substr(2, 9),
      name: draftProduct?.name || 'Untitled Draft',
      category: draftProduct?.category || '',
      material: draftProduct?.material || '',
      image: draftProduct?.image || '',
      enhancedImage: draftProduct?.enhancedImage,
      description: draftProduct?.description || '',
      keywords: draftProduct?.keywords || [],
      recommendedPrice: draftProduct?.recommendedPrice,
      commerceScore: score,
      dimensions: draftProduct?.dimensions,
      status: 'Draft',
      createdAt: new Date().toISOString()
    };
    
    addProduct(newProduct);
    clearDraftProduct();
    navigate('/catalog');
  };

  return (
    <div className="h-full w-full bg-navy-bg relative z-20 flex flex-col">
      <div className="pt-12 px-6 pb-4 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate(-1)} className="text-gray-400 hover:text-white">
            <ArrowLeft size={24} />
          </button>
          <div>
            <h1 className="text-xl font-bold text-white">Final Preview</h1>
          </div>
        </div>
        <button className="text-cyan-400 hover:text-cyan-300 p-2">
          <Eye size={20} />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto no-scrollbar pb-32">
        {/* Product Preview Card */}
        <div className="bg-white mx-6 rounded-3xl overflow-hidden shadow-xl mb-8 relative">
          
          <div className="absolute top-4 right-4 bg-black/60 backdrop-blur-md text-white text-xs font-bold px-3 py-1.5 rounded-full flex items-center gap-1 z-10">
            <CheckCircle size={14} className="text-green-400" />
            Score: {mockAiService.calculateCommerceScore(draftProduct)}
          </div>

          <div className="w-full aspect-square bg-gray-100 relative">
            <img 
              src={draftProduct?.enhancedImage || draftProduct?.image} 
              alt="Product" 
              className="w-full h-full object-cover"
            />
          </div>
          
          <div className="p-6">
            <div className="flex justify-between items-start mb-2">
              <h2 className="text-2xl font-bold text-gray-900 leading-tight">
                {draftProduct?.name || 'Product Name'}
              </h2>
              <span className="text-xl font-bold text-gray-900">
                ₹{draftProduct?.recommendedPrice || '0'}
              </span>
            </div>
            
            <p className="text-sm text-cyan-700 font-medium mb-4">
              {draftProduct?.category} • {draftProduct?.material}
            </p>
            
            <p className="text-gray-600 text-sm leading-relaxed mb-6">
              {draftProduct?.description || 'No description provided.'}
            </p>
            
            {draftProduct?.dimensions && (
              <div className="bg-gray-50 p-3 rounded-xl mb-6">
                <span className="text-xs text-gray-500 uppercase tracking-wider block mb-1">Dimensions</span>
                <span className="text-sm font-medium text-gray-900">{draftProduct.dimensions}</span>
              </div>
            )}
            
            <div>
              <span className="text-xs text-gray-500 uppercase tracking-wider block mb-2">Search Keywords</span>
              <div className="flex flex-wrap gap-2">
                {draftProduct?.keywords?.map((k, i) => (
                  <span key={i} className="bg-gray-100 text-gray-700 text-[10px] px-2 py-1 rounded-md font-medium border border-gray-200">
                    {k}
                  </span>
                ))}
              </div>
            </div>
          </div>
        </div>

        <div className="px-6 space-y-4">
          <button 
            onClick={handlePublish}
            disabled={isPublishing || showSuccess}
            className="w-full bg-gradient-to-r from-cyan-500 to-blue-600 text-white rounded-full py-4 font-bold flex items-center justify-center gap-2 hover:opacity-90 shadow-glow-blue disabled:opacity-50"
          >
            {isPublishing ? 'Publishing...' : (
              <>
                <Globe size={20} /> Publish to Market
              </>
            )}
          </button>
          
          <button 
            onClick={handleSaveDraft}
            disabled={isPublishing || showSuccess}
            className="w-full glass-card text-white py-4 font-medium hover:bg-white/5"
          >
            Save as Draft
          </button>
        </div>
      </div>

      {/* Success Overlay */}
      <AnimatePresence>
        {showSuccess && (
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="absolute inset-0 z-50 bg-navy-bg/90 backdrop-blur-md flex flex-col items-center justify-center px-6"
          >
            <motion.div 
              initial={{ scale: 0.5, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              transition={{ type: "spring", damping: 15 }}
              className="w-24 h-24 bg-green-500/20 text-green-400 rounded-full flex items-center justify-center mb-6 border-4 border-green-500/50 shadow-[0_0_50px_rgba(34,197,94,0.4)]"
            >
              <CheckCircle size={48} />
            </motion.div>
            <h2 className="text-3xl font-bold text-white mb-2 text-center">Product Published!</h2>
            <p className="text-gray-300 text-center mb-8">Your product is now commerce-ready and live on the marketplace.</p>
            
            <div className="flex gap-4 w-full max-w-xs">
              <button className="flex-1 glass-card py-3 flex items-center justify-center gap-2 text-white">
                <Share2 size={18} /> Share
              </button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
