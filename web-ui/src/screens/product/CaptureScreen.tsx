import React, { useState, useRef } from 'react';
import { motion } from 'framer-motion';
import { Camera, Image as ImageIcon, ArrowLeft, RefreshCw, Check } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../../store/useStore';

export default function CaptureScreen() {
  const navigate = useNavigate();
  const setDraftProduct = useStore(state => state.setDraftProduct);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleImageCapture = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleUsePhoto = () => {
    if (imagePreview) {
      setDraftProduct({ image: imagePreview });
      navigate('/create/enhance');
    }
  };

  return (
    <div className="h-full w-full bg-navy-bg relative z-20 flex flex-col">
      {/* Header */}
      <div className="pt-12 px-6 pb-4 flex items-center gap-4">
        <button onClick={() => navigate(-1)} className="text-gray-400 hover:text-white">
          <ArrowLeft size={24} />
        </button>
        <div>
          <h1 className="text-xl font-bold text-white">Create Product</h1>
          <p className="text-[10px] text-gray-400">Start with a photo of your handmade product.</p>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 px-6 flex flex-col justify-center pb-24">
        {imagePreview ? (
          <motion.div 
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="w-full aspect-[4/5] rounded-[2rem] overflow-hidden border-2 border-cyan-500/30 relative shadow-glow-cyan"
          >
            <img src={imagePreview} alt="Preview" className="w-full h-full object-cover" />
            
            <div className="absolute bottom-6 left-0 right-0 flex justify-center gap-4 px-6">
              <button 
                onClick={() => setImagePreview(null)}
                className="flex-1 glass-card py-3 flex items-center justify-center gap-2 text-white hover:bg-white/10"
              >
                <RefreshCw size={18} />
                <span className="font-medium">Retake</span>
              </button>
              <button 
                onClick={handleUsePhoto}
                className="flex-1 bg-cyan-500 text-black rounded-full py-3 flex items-center justify-center gap-2 font-bold hover:bg-cyan-400 shadow-lg"
              >
                <Check size={18} />
                <span>Use Photo</span>
              </button>
            </div>
          </motion.div>
        ) : (
          <div className="space-y-6">
            {/* Demo Product Setup Instruction */}
            <div className="glass-card p-6 text-center border-dashed border-2 border-gray-600">
              <Camera size={48} className="mx-auto text-gray-500 mb-4" />
              <h2 className="text-white font-medium mb-2">Capture Product</h2>
              <p className="text-gray-400 text-xs mb-6 max-w-[200px] mx-auto">
                Place your product in good lighting and take a clear photo.
              </p>
              
              <input 
                type="file"
                accept="image/*"
                capture="environment"
                className="hidden"
                ref={fileInputRef}
                onChange={handleImageCapture}
              />
              
              <div className="flex flex-col gap-4">
                <button 
                  onClick={() => fileInputRef.current?.click()}
                  className="w-full bg-blue-600 text-white rounded-full py-4 font-bold flex items-center justify-center gap-2 hover:bg-blue-500 shadow-glow-blue"
                >
                  <Camera size={20} />
                  Open Camera
                </button>
                
                <button 
                  onClick={() => fileInputRef.current?.removeAttribute('capture') || fileInputRef.current?.click()}
                  className="w-full glass-card text-white py-4 font-medium flex items-center justify-center gap-2 hover:bg-white/5"
                >
                  <ImageIcon size={20} className="text-cyan-400" />
                  Upload from Gallery
                </button>
              </div>
            </div>

            {/* Demo Hint */}
            <div className="text-center">
              <p className="text-[10px] text-gray-500 italic">
                For demo: Upload any image to simulate the Bamboo Basket journey.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
