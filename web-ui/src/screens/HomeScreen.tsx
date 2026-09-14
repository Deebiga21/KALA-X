import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Bell, Camera, ArrowRight, Store, TrendingUp, Box, IndianRupee, Star } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import TextType from '../components/TextType';
import LiquidGlassCluster from '../components/LiquidGlassCluster';

// Simple animated counter component
export function Counter({ from, to, decimals = 0 }: { from: number, to: number, decimals?: number }) {
  const [count, setCount] = useState(from);

  useEffect(() => {
    let startTime: number;
    const duration = 2000;

    const animate = (timestamp: number) => {
      if (!startTime) startTime = timestamp;
      const progress = timestamp - startTime;
      const percentage = Math.min(progress / duration, 1);
      
      const ease = 1 - Math.pow(1 - percentage, 4);
      
      setCount(from + (to - from) * ease);

      if (progress < duration) {
        requestAnimationFrame(animate);
      } else {
        setCount(to);
      }
    };

    requestAnimationFrame(animate);
  }, [from, to]);

  return <>{count.toFixed(decimals)}</>;
}

const ARTISAN_IMAGE = "https://images.unsplash.com/photo-1605335193910-14e393557eec?q=80&w=600&auto=format&fit=crop";

export default function HomeScreen() {
  const navigate = useNavigate();
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    setLoaded(true);
  }, []);

  const containerVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { 
      opacity: 1, 
      y: 0,
      transition: { duration: 0.6, staggerChildren: 0.1 }
    }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.5 } }
  };

  return (
    <div className="absolute inset-0 overflow-hidden">
      {/* Animated Background Gradients */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <motion.div 
          animate={{ scale: [1, 1.2, 1], opacity: [0.3, 0.5, 0.3] }}
          transition={{ duration: 8, repeat: Infinity, ease: "easeInOut" }}
          className="absolute top-[-10%] left-[-20%] w-[300px] h-[300px] bg-blue-600/30 rounded-full blur-[80px]"
        />
        <motion.div 
          animate={{ scale: [1, 1.3, 1], opacity: [0.2, 0.4, 0.2] }}
          transition={{ duration: 10, repeat: Infinity, ease: "easeInOut", delay: 1 }}
          className="absolute top-[40%] right-[-20%] w-[250px] h-[250px] bg-cyan-500/20 rounded-full blur-[80px]"
        />
        <motion.div 
          animate={{ scale: [1, 1.2, 1], opacity: [0.2, 0.4, 0.2] }}
          transition={{ duration: 9, repeat: Infinity, ease: "easeInOut", delay: 2 }}
          className="absolute bottom-[-10%] left-[10%] w-[350px] h-[350px] bg-violet-600/20 rounded-full blur-[80px]"
        />
      </div>

      {/* Content Scroll Area - increased pb-32 to avoid nav overlap */}
      <motion.div 
        className="h-full w-full overflow-y-auto no-scrollbar pb-32 relative z-10"
        initial="hidden"
        animate={loaded ? "visible" : "hidden"}
        variants={containerVariants}
      >
        {/* Header */}
        <motion.header variants={itemVariants} className="flex justify-between items-center px-6 pt-12 pb-4">
          <div className="flex items-center gap-3">
            <div className="relative w-10 h-10 flex items-center justify-center">
              <div className="absolute inset-0 bg-gradient-to-tr from-cyan-400 to-blue-600 rounded-lg opacity-20 blur-sm"></div>
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-8 h-8 text-cyan-400 z-10 relative">
                <path d="M12 2L15 8L21 9L16.5 14L18 20L12 17L6 20L7.5 14L3 9L9 8L12 2Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <div className="flex flex-col">
              <div className="flex items-center">
                <span className="text-xl font-bold tracking-wider text-white">KALA</span>
                <span className="text-xl font-bold text-cyan-400 ml-1">-X</span>
              </div>
              <span className="text-[10px] text-gray-400 font-medium">From Handmade to Market-Ready</span>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <button className="relative text-gray-300 hover:text-white transition-colors">
              <Bell size={20} />
              <span className="absolute top-0 right-0 w-2 h-2 bg-red-500 rounded-full border-2 border-transparent"></span>
            </button>
          </div>
        </motion.header>

        {/* Hero Section */}
        <motion.section variants={itemVariants} className="px-6 py-6">
          <h1 className="text-[2.75rem] leading-[1.1] font-bold mb-4 tracking-tight">
            <motion.span variants={itemVariants} className="block text-white">Turn your</motion.span>
            <motion.span variants={itemVariants} className="block text-cyan-400">craft <span className="text-white">into</span></motion.span>
            <TextType 
              as="div"
              text={["commerce.", "a business.", "growth.", "success."]}
              typingSpeed={80}
              deletingSpeed={40}
              pauseDuration={2500}
              className="block text-blue-400"
            />
          </h1>
          
          <p className="text-gray-300 text-sm leading-relaxed max-w-[280px] mb-8">
            AI-powered support for marginalized artisans to create, price and sell their handmade products.
          </p>

          <motion.div 
            className="relative w-full h-[320px] rounded-[2rem] overflow-hidden shadow-2xl border border-white/10 mt-6"
            whileHover={{ scale: 1.02 }}
            transition={{ duration: 0.3 }}
          >
            {/* Liquid Glass Background */}
            <div className="absolute inset-0 opacity-80">
              <LiquidGlassCluster />
            </div>
            
            <div className="absolute inset-0 bg-black/40"></div>

            {/* AI Product Studio Flow */}
            <div className="absolute inset-0 flex flex-col items-center justify-center p-4 z-10">
              <h2 className="text-white font-bold tracking-widest text-sm mb-4 bg-black/40 px-3 py-1 rounded-full border border-white/20 backdrop-blur-sm">AI PRODUCT STUDIO</h2>
              
              <div className="flex flex-col items-center justify-center space-y-1">
                <span className="text-gray-300 text-xs font-semibold">RAW PRODUCT</span>
                <span className="text-cyan-400 text-xs">↓</span>
                <span className="text-white text-xs font-semibold drop-shadow-md">AI ENHANCEMENT</span>
                <span className="text-cyan-400 text-xs">↓</span>
                <span className="text-gray-300 text-xs font-semibold">SMART CATALOG</span>
                <span className="text-cyan-400 text-xs">↓</span>
                <span className="text-white text-xs font-semibold drop-shadow-md">SMART PRICE</span>
                <span className="text-cyan-400 text-xs">↓</span>
                <span className="text-blue-400 text-xs font-bold drop-shadow-md">MARKET READY</span>
              </div>
            </div>
          </motion.div>
        </motion.section>

        {/* Action Cards */}
        <motion.section variants={itemVariants} className="px-6 space-y-4">
          <motion.button 
            whileTap={{ scale: 0.97 }}
            onClick={() => navigate('/create')}
            className="w-full group relative overflow-hidden rounded-[1.5rem] p-[1px]"
          >
            <div className="absolute inset-0 bg-gradient-to-r from-blue-500 via-cyan-400 to-blue-500 opacity-50 group-hover:opacity-100 transition-opacity duration-500 blur-sm"></div>
            <div className="relative h-full w-full bg-navy-bg/90 backdrop-blur-xl rounded-[1.5rem] p-5 flex items-center justify-between border border-blue-500/30">
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 rounded-full bg-blue-500/20 flex items-center justify-center text-blue-400">
                  <Camera size={24} />
                </div>
                <div className="text-left">
                  <h3 className="text-white font-semibold text-lg">Create Product</h3>
                  <p className="text-gray-400 text-xs mt-1">Capture, describe and let AI do the rest.</p>
                </div>
              </div>
              <ArrowRight size={20} className="text-gray-400 group-hover:text-white transition-colors group-hover:translate-x-1 duration-300" />
            </div>
          </motion.button>

          <motion.button 
            whileTap={{ scale: 0.97 }}
            onClick={() => navigate('/catalog')}
            className="w-full group glass-card p-5 flex items-center justify-between hover:border-cyan-500/30 transition-colors bg-white/5 rounded-[1.5rem]"
          >
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-cyan-500/10 flex items-center justify-center text-cyan-400">
                <Store size={24} />
              </div>
              <div className="text-left">
                <h3 className="text-white font-semibold text-lg">My Catalog</h3>
                <p className="text-gray-400 text-xs mt-1">Manage your products & listings.</p>
              </div>
            </div>
            <ArrowRight size={20} className="text-gray-400 group-hover:text-white transition-colors group-hover:translate-x-1 duration-300" />
          </motion.button>

          <motion.button 
            whileTap={{ scale: 0.97 }}
            onClick={() => navigate('/insights')}
            className="w-full group glass-card p-5 flex items-center justify-between hover:border-violet-500/30 transition-colors bg-white/5 rounded-[1.5rem]"
          >
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-violet-500/10 flex items-center justify-center text-violet-400">
                <TrendingUp size={24} />
              </div>
              <div className="text-left">
                <h3 className="text-white font-semibold text-lg">Market Insights</h3>
                <p className="text-gray-400 text-xs mt-1">Trends, demand & best pricing.</p>
              </div>
            </div>
            <ArrowRight size={20} className="text-gray-400 group-hover:text-white transition-colors group-hover:translate-x-1 duration-300" />
          </motion.button>
        </motion.section>

        {/* Stats Section */}
        <motion.section variants={itemVariants} className="px-6 mt-6">
          <div className="glass-card p-5 grid grid-cols-3 divide-x divide-white/10 bg-white/5 rounded-[1.5rem]">
            <div className="flex flex-col items-center justify-center px-2">
              <div className="text-blue-400 mb-2">
                <Box size={20} />
              </div>
              <span className="text-xl font-bold text-white mb-1">
                <Counter from={0} to={12} />+
              </span>
              <span className="text-[10px] text-gray-400 text-center leading-tight">Products Created</span>
            </div>
            <div className="flex flex-col items-center justify-center px-2">
              <div className="text-cyan-400 mb-2">
                <IndianRupee size={20} />
              </div>
              <span className="text-xl font-bold text-white mb-1">
                <Counter from={0} to={24.6} decimals={1} />K
              </span>
              <span className="text-[10px] text-gray-400 text-center leading-tight">Total Sales</span>
            </div>
            <div className="flex flex-col items-center justify-center px-2">
              <div className="text-violet-400 mb-2">
                <Star size={20} />
              </div>
              <span className="text-xl font-bold text-white mb-1">
                <Counter from={0} to={91} />/100
              </span>
              <span className="text-[10px] text-gray-400 text-center leading-tight">Commerce Score</span>
            </div>
          </div>
        </motion.section>

        {/* Bottom Illustration area */}
        <motion.section variants={itemVariants} className="px-6 mt-10 mb-8 flex flex-col items-center justify-center text-center relative h-32 overflow-hidden rounded-2xl mx-6">
           <div className="absolute inset-0 bg-gradient-to-t from-blue-900/40 to-transparent pointer-events-none rounded-2xl border border-blue-500/20"></div>
           
           <div className="absolute bottom-0 left-0 w-full opacity-30">
             <svg viewBox="0 0 1440 320" className="w-full h-auto text-cyan-600 fill-current">
               <path d="M0,224L48,213.3C96,203,192,181,288,186.7C384,192,480,224,576,234.7C672,245,768,235,864,208C960,181,1056,139,1152,133.3C1248,128,1344,160,1392,176L1440,192L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path>
             </svg>
           </div>

           <div className="relative z-10 flex flex-col items-center">
             <p className="text-xs text-cyan-300 italic mb-1 font-medium">Supporting Artisans.</p>
             <p className="text-xs text-white font-medium">Building a Better Tomorrow.</p>
           </div>
        </motion.section>
      </motion.div>
    </div>
  );
}
