import React from 'react';
import { motion } from 'framer-motion';
import { TrendingUp, ArrowUpRight, BarChart3, Clock } from 'lucide-react';

export default function InsightsScreen() {
  return (
    <div className="h-full w-full overflow-y-auto no-scrollbar pb-32 relative z-10 px-6 pt-12">
      <h1 className="text-3xl font-bold text-white mb-2">Market Insights</h1>
      <p className="text-gray-400 text-sm mb-6">Discover trends and demand.</p>
      
      {/* Best Opportunity Card */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="relative overflow-hidden rounded-[1.5rem] p-[1px] mb-6"
      >
        <div className="absolute inset-0 bg-gradient-to-r from-violet-500 via-fuchsia-500 to-violet-500 opacity-50 blur-sm"></div>
        <div className="relative h-full w-full bg-navy-bg/90 backdrop-blur-xl rounded-[1.5rem] p-5 border border-violet-500/30">
          <div className="flex items-center gap-2 text-violet-400 mb-2">
            <TrendingUp size={16} />
            <span className="text-xs font-bold uppercase tracking-wider">Best Opportunity</span>
          </div>
          <h2 className="text-2xl font-bold text-white mb-4">Bamboo Home Decor</h2>
          
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-400">Demand Level</span>
              <span className="text-sm font-bold text-green-400">High</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-400">Avg Market Price</span>
              <span className="text-sm font-medium text-white">₹820 – ₹920</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-400">Recommended Price</span>
              <span className="text-sm font-bold text-cyan-400">₹849</span>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Trending Categories */}
      <h3 className="text-lg font-bold text-white mb-4">Trending Categories</h3>
      <div className="space-y-3 mb-8">
        {[
          { name: "Bamboo Crafts", up: "18%" },
          { name: "Handwoven Bags", up: "12%" },
          { name: "Terracotta Products", up: "9%" }
        ].map((item, i) => (
          <motion.div 
            key={i}
            initial={{ opacity: 0, x: -10 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: i * 0.1 }}
            className="glass-card p-4 flex justify-between items-center"
          >
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-white/5 flex items-center justify-center">
                <BarChart3 size={18} className="text-blue-400" />
              </div>
              <span className="text-white font-medium">{item.name}</span>
            </div>
            <div className="flex items-center gap-1 text-green-400">
              <ArrowUpRight size={16} />
              <span className="font-bold">{item.up}</span>
            </div>
          </motion.div>
        ))}
      </div>

      <div className="glass-card p-5 border-l-4 border-l-cyan-500">
        <div className="flex gap-3">
          <Clock className="text-cyan-400 mt-1" size={20} />
          <div>
            <h4 className="text-white font-bold mb-1">Best time to sell</h4>
            <p className="text-xs text-gray-400">High demand expected this week for eco-friendly home decor. Consider listing new items now.</p>
          </div>
        </div>
      </div>
    </div>
  );
}
