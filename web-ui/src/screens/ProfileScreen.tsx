import React from 'react';
import { User, Settings, Bell, HelpCircle, Languages, Package, LogOut } from 'lucide-react';
import { useStore } from '../store/useStore';

export default function ProfileScreen() {
  const products = useStore((state) => state.products);

  const stats = [
    { label: "Products", value: products.length },
    { label: "Sales", value: "₹24.6K" },
    { label: "Score", value: "91" }
  ];

  const menuItems = [
    { icon: <User size={20} />, label: "Edit Profile" },
    { icon: <Languages size={20} />, label: "Language (Tamil)" },
    { icon: <Package size={20} />, label: "My Products" },
    { icon: <Bell size={20} />, label: "Notifications" },
    { icon: <HelpCircle size={20} />, label: "Help & Support" },
    { icon: <Settings size={20} />, label: "Settings" }
  ];

  return (
    <div className="h-full w-full overflow-y-auto no-scrollbar pb-32 relative z-10 px-6 pt-12">
      <h1 className="text-3xl font-bold text-white mb-6">Profile</h1>
      
      {/* Profile Header */}
      <div className="flex items-center gap-4 mb-8">
        <div className="w-20 h-20 rounded-full bg-gradient-to-tr from-cyan-400 to-blue-600 p-1">
          <div className="w-full h-full rounded-full bg-navy-bg overflow-hidden border-2 border-transparent">
            <img 
              src="https://images.unsplash.com/photo-1534067783941-51c9c23ecefd?q=80&w=200&auto=format&fit=crop" 
              alt="Profile" 
              className="w-full h-full object-cover"
            />
          </div>
        </div>
        <div>
          <h2 className="text-xl font-bold text-white">Lakshmi Devi</h2>
          <p className="text-gray-400 text-sm">Madurai, Tamil Nadu</p>
        </div>
      </div>

      {/* Stats */}
      <div className="glass-card p-5 grid grid-cols-3 divide-x divide-white/10 mb-8">
        {stats.map((stat, i) => (
          <div key={i} className="flex flex-col items-center justify-center">
            <span className="text-xl font-bold text-white">{stat.value}</span>
            <span className="text-[10px] text-gray-400">{stat.label}</span>
          </div>
        ))}
      </div>

      {/* Menu Options */}
      <div className="glass-card overflow-hidden">
        {menuItems.map((item, i) => (
          <button 
            key={i}
            className="w-full flex items-center justify-between p-4 border-b border-white/5 hover:bg-white/5 transition-colors text-left"
          >
            <div className="flex items-center gap-3 text-gray-300">
              <div className="text-cyan-400">{item.icon}</div>
              <span className="font-medium">{item.label}</span>
            </div>
            <span className="text-gray-500">›</span>
          </button>
        ))}
        <button 
          className="w-full flex items-center gap-3 p-4 text-red-400 hover:bg-red-500/10 transition-colors text-left"
        >
          <LogOut size={20} />
          <span className="font-medium">Sign Out</span>
        </button>
      </div>
    </div>
  );
}
