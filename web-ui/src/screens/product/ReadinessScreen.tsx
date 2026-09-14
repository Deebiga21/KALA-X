import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { ArrowLeft, ArrowRight, AlertTriangle, CheckCircle2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../../store/useStore';
import { mockAiService } from '../../services/mockAiService';

export default function ReadinessScreen() {
  const navigate = useNavigate();
  const draftProduct = useStore(state => state.draftProduct);
  const setDraftProduct = useStore(state => state.setDraftProduct);
  
  const [score, setScore] = useState(0);
  const [dimensions, setDimensions] = useState(draftProduct?.dimensions || '');
  
  useEffect(() => {
    // Calculate initial score
    const currentScore = mockAiService.calculateCommerceScore(draftProduct);
    
    // Animate score from 0 to currentScore
    let start = 0;
    const duration = 1500;
    const startTime = performance.now();
    
    const animate = (time: number) => {
      const elapsed = time - startTime;
      const progress = Math.min(elapsed / duration, 1);
      const easeOutQuart = 1 - Math.pow(1 - progress, 4);
      setScore(Math.floor(currentScore * easeOutQuart));
      
      if (progress < 1) {
        requestAnimationFrame(animate);
      }
    };
    
    requestAnimationFrame(animate);
  }, [draftProduct]);

  const handleUpdateDimensions = () => {
    if (dimensions.trim()) {
      setDraftProduct({ dimensions });
    }
  };

  const getStatusColor = (current: number, target: number) => {
    if (current >= target) return 'text-green-400';
    if (current > 0) return 'text-yellow-400';
    return 'text-red-400';
  };

  return (
    <div className="h-full w-full bg-navy-bg relative z-20 flex flex-col">
      <div className="pt-12 px-6 pb-4 flex items-center gap-4">
        <button onClick={() => navigate(-1)} className="text-gray-400 hover:text-white">
          <ArrowLeft size={24} />
        </button>
        <div>
          <h1 className="text-xl font-bold text-white">Commerce Readiness</h1>
          <p className="text-[10px] text-gray-400">Ensure your product is ready for buyers.</p>
        </div>
      </div>

      <div className="flex-1 px-6 overflow-y-auto no-scrollbar pb-24 pt-6">
        
        {/* Circular Progress */}
        <div className="flex justify-center mb-10">
          <div className="relative w-48 h-48">
            <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
              <circle 
                cx="50" cy="50" r="45" 
                fill="none" stroke="rgba(255,255,255,0.1)" strokeWidth="8" 
              />
              <motion.circle 
                cx="50" cy="50" r="45" 
                fill="none" 
                stroke={score >= 90 ? '#10b981' : score >= 70 ? '#3b82f6' : '#ef4444'} 
                strokeWidth="8"
                strokeLinecap="round"
                initial={{ strokeDasharray: '0 283' }}
                animate={{ strokeDasharray: `${(score / 100) * 283} 283` }}
                transition={{ duration: 1.5, ease: "easeOut" }}
              />
            </svg>
            <div className="absolute inset-0 flex flex-col items-center justify-center">
              <span className="text-5xl font-bold text-white">{score}</span>
              <span className="text-xs text-gray-400 uppercase tracking-wider">Out of 100</span>
            </div>
          </div>
        </div>

        <div className="text-center mb-8">
          <h2 className="text-2xl font-bold text-white mb-1">
            {score >= 90 ? 'Ready to Publish! 🎉' : 'Needs Improvement'}
          </h2>
          <p className="text-sm text-gray-400">
            {score >= 90 
              ? 'Your product meets all e-commerce standards.' 
              : 'Complete the missing fields to improve your score.'}
          </p>
        </div>

        {/* Score Breakdown */}
        <div className="glass-card p-5 space-y-4 mb-6">
          <h3 className="text-sm font-bold text-white mb-4">Readiness Breakdown</h3>
          
          <div className="space-y-3">
            {[
              { label: 'Product Image (AI Enhanced)', points: 30, max: 30, has: !!draftProduct?.enhancedImage },
              { label: 'Title & Category', points: 20, max: 20, has: !!draftProduct?.name && !!draftProduct?.category },
              { label: 'Description & SEO', points: 20, max: 20, has: !!draftProduct?.description },
              { label: 'Smart Pricing', points: 20, max: 20, has: !!draftProduct?.recommendedPrice },
              { label: 'Product Dimensions', points: draftProduct?.dimensions ? 10 : 0, max: 10, has: !!draftProduct?.dimensions }
            ].map((item, i) => (
              <div key={i} className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  {item.has ? <CheckCircle2 size={16} className="text-green-400" /> : <AlertTriangle size={16} className="text-yellow-400" />}
                  <span className={item.has ? 'text-gray-300' : 'text-yellow-200'}>{item.label}</span>
                </div>
                <span className={`text-sm font-bold ${getStatusColor(item.points, item.max)}`}>
                  {item.points}/{item.max}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Actionable items */}
        {!draftProduct?.dimensions && (
          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-yellow-500/10 border border-yellow-500/30 rounded-2xl p-5 mb-8"
          >
            <div className="flex items-start gap-3 mb-4">
              <AlertTriangle size={20} className="text-yellow-400 shrink-0" />
              <div>
                <h4 className="text-yellow-400 font-bold text-sm">Missing Information</h4>
                <p className="text-xs text-yellow-200/70 mt-1">Add product dimensions to boost your score by 10 points and increase buyer trust.</p>
              </div>
            </div>
            
            <div className="flex gap-2">
              <input 
                type="text" 
                placeholder="e.g. 12 x 8 x 6 inches" 
                value={dimensions}
                onChange={(e) => setDimensions(e.target.value)}
                className="flex-1 bg-black/40 border border-yellow-500/20 rounded-lg px-3 py-2 text-white text-sm outline-none focus:border-yellow-400"
              />
              <button 
                onClick={handleUpdateDimensions}
                className="bg-yellow-500 text-black px-4 py-2 rounded-lg font-bold text-sm hover:bg-yellow-400"
              >
                Add
              </button>
            </div>
          </motion.div>
        )}

        <button 
          onClick={() => navigate('/create/final')}
          className="w-full bg-cyan-500 text-black rounded-full py-4 font-bold flex items-center justify-center gap-2 hover:bg-cyan-400 shadow-lg"
        >
          Review Final Listing
          <ArrowRight size={18} />
        </button>

      </div>
    </div>
  );
}
