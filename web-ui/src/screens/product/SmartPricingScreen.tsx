import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { ArrowLeft, ArrowRight, IndianRupee, TrendingUp, Info } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../../store/useStore';
import { mockAiService } from '../../services/mockAiService';

export default function SmartPricingScreen() {
  const navigate = useNavigate();
  const draftProduct = useStore(state => state.draftProduct);
  const setDraftProduct = useStore(state => state.setDraftProduct);
  
  const [costs, setCosts] = useState({
    raw: 300,
    labour: 200,
    packaging: 50,
    other: 50
  });

  const [pricingData, setPricingData] = useState<{
    totalCost: number;
    recommendedPrice: number;
    marketMin: number;
    marketMax: number;
    confidence: number;
  } | null>(null);

  const [isCalculating, setIsCalculating] = useState(false);
  const [finalPrice, setFinalPrice] = useState(0);

  const handleCalculate = async () => {
    setIsCalculating(true);
    const result = await mockAiService.calculatePrice(costs);
    setPricingData(result);
    setFinalPrice(result.recommendedPrice);
    setIsCalculating(false);
  };

  const handleContinue = () => {
    setDraftProduct({
      rawMaterialCost: costs.raw,
      labourCost: costs.labour,
      packagingCost: costs.packaging,
      otherCost: costs.other,
      totalCost: pricingData?.totalCost,
      recommendedPrice: finalPrice,
      marketMin: pricingData?.marketMin,
      marketMax: pricingData?.marketMax,
      pricingConfidence: pricingData?.confidence
    });
    navigate('/create/readiness');
  };

  return (
    <div className="h-full w-full bg-navy-bg relative z-20 flex flex-col">
      <div className="pt-12 px-6 pb-4 flex items-center gap-4">
        <button onClick={() => navigate(-1)} className="text-gray-400 hover:text-white">
          <ArrowLeft size={24} />
        </button>
        <div>
          <h1 className="text-xl font-bold text-white">Smart Pricing</h1>
          <p className="text-[10px] text-gray-400">Find a fair and competitive selling price.</p>
        </div>
      </div>

      <div className="flex-1 px-6 overflow-y-auto no-scrollbar pb-24 pt-4">
        
        {/* Cost Inputs */}
        <div className="glass-card p-5 space-y-4 mb-6">
          <h2 className="text-sm font-bold text-white mb-2">Input Costs (₹)</h2>
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-xs text-gray-400 block mb-1">Raw Material</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">₹</span>
                <input 
                  type="number" 
                  value={costs.raw}
                  onChange={(e) => setCosts({...costs, raw: parseInt(e.target.value) || 0})}
                  className="w-full bg-black/50 border border-white/10 rounded-lg py-2 pl-8 pr-3 text-white outline-none focus:border-cyan-400"
                />
              </div>
            </div>
            <div>
              <label className="text-xs text-gray-400 block mb-1">Labour (Time)</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">₹</span>
                <input 
                  type="number" 
                  value={costs.labour}
                  onChange={(e) => setCosts({...costs, labour: parseInt(e.target.value) || 0})}
                  className="w-full bg-black/50 border border-white/10 rounded-lg py-2 pl-8 pr-3 text-white outline-none focus:border-cyan-400"
                />
              </div>
            </div>
            <div>
              <label className="text-xs text-gray-400 block mb-1">Packaging</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">₹</span>
                <input 
                  type="number" 
                  value={costs.packaging}
                  onChange={(e) => setCosts({...costs, packaging: parseInt(e.target.value) || 0})}
                  className="w-full bg-black/50 border border-white/10 rounded-lg py-2 pl-8 pr-3 text-white outline-none focus:border-cyan-400"
                />
              </div>
            </div>
            <div>
              <label className="text-xs text-gray-400 block mb-1">Other</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">₹</span>
                <input 
                  type="number" 
                  value={costs.other}
                  onChange={(e) => setCosts({...costs, other: parseInt(e.target.value) || 0})}
                  className="w-full bg-black/50 border border-white/10 rounded-lg py-2 pl-8 pr-3 text-white outline-none focus:border-cyan-400"
                />
              </div>
            </div>
          </div>

          <div className="pt-4 border-t border-white/10 flex justify-between items-center">
            <span className="text-white font-medium">Total Cost:</span>
            <span className="text-lg font-bold text-white">₹{costs.raw + costs.labour + costs.packaging + costs.other}</span>
          </div>

          {!pricingData && (
            <button 
              onClick={handleCalculate}
              disabled={isCalculating}
              className="w-full mt-4 bg-cyan-500/20 text-cyan-400 border border-cyan-500/30 rounded-lg py-3 font-medium flex justify-center items-center gap-2 hover:bg-cyan-500/30 transition-colors"
            >
              {isCalculating ? (
                <>Calculating Market Data...</>
              ) : (
                <>
                  <TrendingUp size={18} /> Generate AI Price
                </>
              )}
            </button>
          )}
        </div>

        {/* AI Recommendations */}
        {pricingData && (
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-6"
          >
            <div className="glass-card p-6 border border-cyan-500/30 shadow-glow-cyan relative overflow-hidden">
              <div className="absolute top-0 right-0 bg-cyan-500 text-black text-[10px] font-bold px-3 py-1 rounded-bl-lg">
                {pricingData.confidence}% Confidence
              </div>
              
              <h2 className="text-center text-gray-400 text-sm mb-2">Recommended Price</h2>
              <div className="flex justify-center items-end gap-1 mb-6 text-cyan-400">
                <IndianRupee size={28} className="mb-1" />
                <span className="text-5xl font-bold">{pricingData.recommendedPrice}</span>
              </div>

              <div className="space-y-2 mb-6">
                <div className="flex justify-between text-xs text-gray-400">
                  <span>Cost (₹{pricingData.totalCost})</span>
                  <span>Market Avg (₹{Math.round((pricingData.marketMin + pricingData.marketMax)/2)})</span>
                </div>
                {/* Visual Gauge */}
                <div className="w-full h-2 bg-gray-800 rounded-full relative">
                  <div className="absolute left-0 top-0 h-full bg-gray-500 rounded-l-full" style={{ width: '30%' }}></div>
                  <div className="absolute left-[30%] top-0 h-full bg-gradient-to-r from-cyan-500 to-blue-500" style={{ width: '40%' }}></div>
                  <div className="absolute left-[50%] top-1/2 -translate-y-1/2 w-4 h-4 bg-white rounded-full shadow-lg border-2 border-cyan-500"></div>
                </div>
                <div className="flex justify-between text-[10px] text-gray-500">
                  <span>Loss</span>
                  <span>Fair Market Range (₹{pricingData.marketMin} - ₹{pricingData.marketMax})</span>
                  <span>Premium</span>
                </div>
              </div>

              <div className="flex items-start gap-2 bg-blue-900/20 p-3 rounded-lg border border-blue-500/20">
                <Info size={16} className="text-blue-400 mt-0.5 shrink-0" />
                <p className="text-[11px] text-blue-200 leading-tight">
                  Recommended price is based on product cost, category demand, and competitor pricing signals.
                </p>
              </div>
            </div>

            <div className="flex gap-4">
              <button 
                onClick={() => setPricingData(null)}
                className="flex-1 glass-card py-4 flex items-center justify-center text-white hover:bg-white/10 font-medium"
              >
                Adjust Costs
              </button>
              <button 
                onClick={handleContinue}
                className="flex-[2] bg-cyan-500 text-black rounded-full py-4 font-bold flex items-center justify-center gap-2 hover:bg-cyan-400 shadow-lg"
              >
                Accept ₹{finalPrice}
                <ArrowRight size={18} />
              </button>
            </div>
          </motion.div>
        )}
      </div>
    </div>
  );
}
