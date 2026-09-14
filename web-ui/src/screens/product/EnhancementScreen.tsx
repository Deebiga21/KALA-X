import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { ArrowLeft, Sparkles, CheckCircle2, ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../../store/useStore';
import { mockAiService } from '../../services/mockAiService';

export default function EnhancementScreen() {
  const navigate = useNavigate();
  const draftProduct = useStore(state => state.draftProduct);
  const setDraftProduct = useStore(state => state.setDraftProduct);
  
  const [step, setStep] = useState(0);
  const [enhancedImage, setEnhancedImage] = useState<string | null>(null);

  const steps = [
    "Detecting product...",
    "Removing background...",
    "Correcting lighting...",
    "Improving clarity...",
    "Preparing commerce image..."
  ];

  useEffect(() => {
    let currentStep = 0;
    const interval = setInterval(() => {
      currentStep++;
      if (currentStep < steps.length) {
        setStep(currentStep);
      } else {
        clearInterval(interval);
      }
    }, 800);

    const processImage = async () => {
      if (draftProduct?.image) {
        const result = await mockAiService.enhanceImage(draftProduct.image);
        setEnhancedImage(result);
      }
    };
    
    processImage();

    return () => clearInterval(interval);
  }, [draftProduct]);

  const handleContinue = () => {
    if (enhancedImage) {
      setDraftProduct({ enhancedImage });
      navigate('/create/voice');
    }
  };

  const isComplete = step === steps.length - 1 && enhancedImage !== null;

  return (
    <div className="h-full w-full bg-navy-bg relative z-20 flex flex-col">
      <div className="pt-12 px-6 pb-4 flex items-center gap-4">
        <button onClick={() => navigate(-1)} className="text-gray-400 hover:text-white">
          <ArrowLeft size={24} />
        </button>
        <div>
          <h1 className="text-xl font-bold text-white">AI Enhancement</h1>
          <p className="text-[10px] text-gray-400">Making your product market-ready.</p>
        </div>
      </div>

      <div className="flex-1 px-6 flex flex-col pt-6 pb-24 overflow-y-auto no-scrollbar">
        {/* Images Before / After */}
        <div className="flex gap-4 mb-8 h-[200px]">
          <div className="flex-1 flex flex-col relative">
            <span className="text-[10px] text-gray-400 mb-2 text-center uppercase tracking-wider">Before</span>
            <div className="flex-1 rounded-2xl overflow-hidden glass-card">
              <img src={draftProduct?.image || ''} alt="Original" className="w-full h-full object-cover opacity-60 grayscale-[50%]" />
            </div>
          </div>
          
          <div className="flex-1 flex flex-col relative">
            <span className="text-[10px] text-cyan-400 font-bold mb-2 text-center uppercase tracking-wider">After AI</span>
            <div className="flex-1 rounded-2xl overflow-hidden border-2 border-cyan-500/30 shadow-glow-cyan relative">
              {enhancedImage ? (
                <motion.img 
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  src={enhancedImage} 
                  alt="Enhanced" 
                  className="w-full h-full object-cover" 
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center bg-gray-900">
                  <Sparkles size={24} className="text-cyan-400 animate-pulse" />
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Processing Steps */}
        <div className="glass-card p-6 mb-8 flex-1">
          <div className="flex items-center gap-3 mb-6">
            <div className={`w-8 h-8 rounded-full flex items-center justify-center ${isComplete ? 'bg-green-500/20 text-green-400' : 'bg-cyan-500/20 text-cyan-400 animate-pulse'}`}>
              {isComplete ? <CheckCircle2 size={16} /> : <Sparkles size={16} />}
            </div>
            <h3 className="text-white font-medium">
              {isComplete ? "Enhancement Complete" : "AI is preparing your product..."}
            </h3>
          </div>

          <div className="space-y-4 pl-4">
            {steps.map((s, i) => (
              <div key={i} className="flex items-center gap-3">
                <div className="relative flex flex-col items-center">
                  <div className={`w-4 h-4 rounded-full flex items-center justify-center border ${
                    i < step ? 'border-green-400 bg-green-400/20' : 
                    i === step ? 'border-cyan-400 bg-cyan-400/20 shadow-glow-cyan' : 
                    'border-gray-700 bg-transparent'
                  }`}>
                    {i < step && <CheckCircle2 size={10} className="text-green-400" />}
                    {i === step && <div className="w-1.5 h-1.5 bg-cyan-400 rounded-full animate-ping" />}
                  </div>
                  {i < steps.length - 1 && (
                    <div className={`w-[2px] h-4 mt-1 ${i < step ? 'bg-green-400/50' : 'bg-gray-800'}`} />
                  )}
                </div>
                <span className={`text-sm ${
                  i < step ? 'text-gray-300' : 
                  i === step ? 'text-cyan-400 font-medium' : 
                  'text-gray-600'
                }`}>
                  {s}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Action */}
        <motion.button 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: isComplete ? 1 : 0, y: isComplete ? 0 : 20 }}
          disabled={!isComplete}
          onClick={handleContinue}
          className="w-full bg-gradient-to-r from-cyan-500 to-blue-600 text-white rounded-full py-4 font-bold flex items-center justify-center gap-2 hover:opacity-90 shadow-glow-blue"
        >
          Use Enhanced Image
          <ArrowRight size={20} />
        </motion.button>
      </div>
    </div>
  );
}
