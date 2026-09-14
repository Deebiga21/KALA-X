import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ArrowLeft, Mic, Square, Edit2, Languages, ArrowRight, Loader2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../../store/useStore';
import { mockAiService } from '../../services/mockAiService';
import TextType from '../../components/TextType';

export default function VoiceCatalogScreen() {
  const navigate = useNavigate();
  const draftProduct = useStore(state => state.draftProduct);
  const setDraftProduct = useStore(state => state.setDraftProduct);
  
  const [language, setLanguage] = useState('Tamil');
  const [isRecording, setIsRecording] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const [stage, setStage] = useState<'idle' | 'recording' | 'processing_voice' | 'generating_catalog' | 'review'>('idle');
  
  const [transcription, setTranscription] = useState('');
  const [translation, setTranslation] = useState('');
  
  // Catalog Data Form
  const [formData, setFormData] = useState({
    name: '',
    category: '',
    material: '',
    description: '',
    keywords: ''
  });

  const timerRef = useRef<any>(null);

  const startRecording = () => {
    setStage('recording');
    setIsRecording(true);
    setRecordingTime(0);
    timerRef.current = setInterval(() => {
      setRecordingTime(prev => prev + 1);
    }, 1000);
  };

  const stopRecording = async () => {
    setIsRecording(false);
    clearInterval(timerRef.current);
    
    // Simulate pipeline
    setStage('processing_voice');
    
    const voiceResult = await mockAiService.transcribeAndTranslate(null, language);
    setTranscription(voiceResult.originalText);
    setTranslation(voiceResult.englishTranslation);
    
    setStage('generating_catalog');
    
    const catalogResult = await mockAiService.generateCatalog(voiceResult.englishTranslation);
    setFormData({
      name: catalogResult.name,
      category: catalogResult.category,
      material: catalogResult.material,
      description: catalogResult.description,
      keywords: catalogResult.keywords.join(', ')
    });
    
    setStage('review');
  };

  const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  };

  const handleContinue = () => {
    setDraftProduct({
      name: formData.name,
      category: formData.category,
      material: formData.material,
      description: formData.description,
      keywords: formData.keywords.split(',').map(k => k.trim()),
      originalLanguage: language,
      transcription,
      translation
    });
    navigate('/create/pricing');
  };

  return (
    <div className="h-full w-full bg-navy-bg relative z-20 flex flex-col">
      <div className="pt-12 px-6 pb-4 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate(-1)} className="text-gray-400 hover:text-white">
            <ArrowLeft size={24} />
          </button>
          <div>
            <h1 className="text-xl font-bold text-white">Describe Product</h1>
            <p className="text-[10px] text-gray-400">Speak naturally in your language.</p>
          </div>
        </div>
        
        {stage === 'idle' && (
          <div className="flex items-center gap-2 bg-white/5 rounded-full px-3 py-1 border border-white/10">
            <Languages size={14} className="text-cyan-400" />
            <select 
              className="bg-transparent text-xs text-white outline-none"
              value={language}
              onChange={(e) => setLanguage(e.target.value)}
            >
              <option className="bg-navy-bg">Tamil</option>
              <option className="bg-navy-bg">Hindi</option>
              <option className="bg-navy-bg">Telugu</option>
              <option className="bg-navy-bg">English</option>
            </select>
          </div>
        )}
      </div>

      <div className="flex-1 px-6 overflow-y-auto no-scrollbar pb-24">
        
        {/* State: Idle or Recording */}
        {(stage === 'idle' || stage === 'recording') && (
          <div className="h-full flex flex-col items-center justify-center">
            <div className="glass-card p-6 w-full text-center border-dashed border-2 border-white/10 mb-8">
              <p className="text-gray-400 text-sm mb-4">Example in {language}:</p>
              <p className="text-cyan-200 italic font-medium">"இந்த கூடை மூங்கில் கொண்டு கையால் செய்யப்பட்டது..."</p>
            </div>

            <div className="relative mb-12 flex items-center justify-center h-48 w-full">
              {isRecording && (
                <div className="absolute inset-0 flex items-center justify-center opacity-50">
                   {/* Mock Waveform */}
                   <div className="flex gap-1 items-center h-16">
                     {[1,2,3,4,5,6,7,8].map(i => (
                       <motion.div 
                         key={i}
                         animate={{ height: ['20%', '100%', '20%'] }}
                         transition={{ duration: 0.8 + (i*0.1), repeat: Infinity, ease: "easeInOut" }}
                         className="w-2 bg-cyan-400 rounded-full"
                       />
                     ))}
                   </div>
                </div>
              )}
              
              <button 
                onClick={isRecording ? stopRecording : startRecording}
                className={`relative z-10 w-24 h-24 rounded-full flex items-center justify-center transition-all ${
                  isRecording 
                    ? 'bg-red-500/20 text-red-500 border border-red-500/50 shadow-[0_0_30px_rgba(239,68,68,0.4)]' 
                    : 'bg-gradient-to-tr from-cyan-500 to-blue-600 text-white shadow-glow-blue'
                }`}
              >
                {isRecording ? <Square size={32} /> : <Mic size={32} />}
              </button>
            </div>

            {isRecording && (
              <div className="text-center">
                <span className="text-red-400 font-medium animate-pulse">Recording... {formatTime(recordingTime)}</span>
              </div>
            )}
            
            {!isRecording && stage === 'idle' && (
              <p className="text-gray-500 text-sm">Tap microphone to start</p>
            )}
          </div>
        )}

        {/* State: Processing */}
        {(stage === 'processing_voice' || stage === 'generating_catalog') && (
          <div className="h-full flex flex-col items-center justify-center">
            <Loader2 size={48} className="text-cyan-400 animate-spin mb-6" />
            <h2 className="text-xl font-bold text-white mb-2">
              {stage === 'processing_voice' ? 'Understanding voice...' : 'Generating catalog...'}
            </h2>
            <p className="text-gray-400 text-sm text-center max-w-[250px]">
              KALA-X AI is translating your description and formatting it for e-commerce.
            </p>
          </div>
        )}

        {/* State: Review */}
        {stage === 'review' && (
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="space-y-6 pt-4">
            
            <div className="glass-card p-4">
              <h3 className="text-[10px] text-gray-500 uppercase tracking-wider mb-2 flex items-center gap-2">
                <Mic size={12} /> Original Speech ({language})
              </h3>
              <p className="text-gray-300 text-sm italic">"{transcription}"</p>
              <div className="h-[1px] w-full bg-white/5 my-3" />
              <h3 className="text-[10px] text-gray-500 uppercase tracking-wider mb-2 flex items-center gap-2">
                <Languages size={12} /> English Translation
              </h3>
              <p className="text-gray-300 text-sm">"{translation}"</p>
            </div>

            <div className="glass-card p-5 space-y-4 border border-cyan-500/20">
              <div className="flex justify-between items-center mb-2">
                <h2 className="text-cyan-400 font-bold uppercase text-xs tracking-wider">AI Generated Catalog</h2>
                <Edit2 size={14} className="text-gray-500" />
              </div>

              <div>
                <label className="text-[10px] text-gray-500 uppercase">Product Name</label>
                <input 
                  type="text" 
                  className="w-full bg-transparent border-b border-white/10 text-white font-medium pb-1 outline-none focus:border-cyan-400"
                  value={formData.name}
                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-[10px] text-gray-500 uppercase">Category</label>
                  <input 
                    type="text" 
                    className="w-full bg-transparent border-b border-white/10 text-white text-sm pb-1 outline-none focus:border-cyan-400"
                    value={formData.category}
                    onChange={(e) => setFormData({...formData, category: e.target.value})}
                  />
                </div>
                <div>
                  <label className="text-[10px] text-gray-500 uppercase">Material</label>
                  <input 
                    type="text" 
                    className="w-full bg-transparent border-b border-white/10 text-white text-sm pb-1 outline-none focus:border-cyan-400"
                    value={formData.material}
                    onChange={(e) => setFormData({...formData, material: e.target.value})}
                  />
                </div>
              </div>

              <div>
                <label className="text-[10px] text-gray-500 uppercase">Description</label>
                <textarea 
                  className="w-full bg-transparent border border-white/10 rounded-lg p-3 text-gray-300 text-sm outline-none focus:border-cyan-400 h-24 resize-none mt-1"
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                />
              </div>

              <div>
                <label className="text-[10px] text-gray-500 uppercase">Keywords (SEO)</label>
                <input 
                  type="text" 
                  className="w-full bg-transparent border-b border-white/10 text-cyan-200 text-xs pb-1 outline-none focus:border-cyan-400"
                  value={formData.keywords}
                  onChange={(e) => setFormData({...formData, keywords: e.target.value})}
                />
              </div>
            </div>

            <div className="flex gap-4">
              <button 
                onClick={() => setStage('idle')}
                className="flex-1 glass-card py-4 flex items-center justify-center text-white hover:bg-white/10 font-medium"
              >
                Regenerate
              </button>
              <button 
                onClick={handleContinue}
                className="flex-[2] bg-cyan-500 text-black rounded-full py-4 font-bold flex items-center justify-center gap-2 hover:bg-cyan-400 shadow-lg"
              >
                Continue to Pricing
                <ArrowRight size={18} />
              </button>
            </div>
          </motion.div>
        )}
      </div>
    </div>
  );
}
