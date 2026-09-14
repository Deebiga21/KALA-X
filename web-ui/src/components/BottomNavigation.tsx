import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Home, Grid, BarChart2, User } from 'lucide-react';
import clsx from 'clsx';
import { twMerge } from 'tailwind-merge';
import { motion } from 'framer-motion';

function cn(...inputs: (string | undefined | null | false)[]) {
  return twMerge(clsx(inputs));
}

function NavItem({ icon, label, path, active, onClick }: { icon: React.ReactNode, label: string, path: string, active: boolean, onClick: (path: string) => void }) {
  return (
    <button 
      onClick={() => onClick(path)}
      className={cn(
        "flex flex-col items-center gap-1 transition-all duration-300",
        active ? "text-cyan-400" : "text-gray-500 hover:text-gray-300"
      )}
    >
      <div className="relative">
        {icon}
        {active && (
          <motion.div 
            layoutId="nav-indicator"
            className="absolute -bottom-2 left-1/2 -translate-x-1/2 w-1 h-1 rounded-full bg-cyan-400"
          />
        )}
      </div>
      <span className="text-[10px] font-medium">{label}</span>
    </button>
  );
}

export default function BottomNavigation() {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <div className="absolute bottom-6 left-6 right-6 z-50">
      <div className="glass-card px-6 py-4 flex justify-between items-center rounded-[2rem] bg-navy-bg/90 backdrop-blur-md border border-white/10 shadow-lg">
        <NavItem 
          icon={<Home size={22} />} 
          label="Home" 
          path="/"
          active={location.pathname === '/'} 
          onClick={navigate} 
        />
        <NavItem 
          icon={<Grid size={22} />} 
          label="Catalog"
          path="/catalog" 
          active={location.pathname === '/catalog'} 
          onClick={navigate} 
        />
        <NavItem 
          icon={<BarChart2 size={22} />} 
          label="Insights" 
          path="/insights"
          active={location.pathname === '/insights'} 
          onClick={navigate} 
        />
        <NavItem 
          icon={<User size={22} />} 
          label="Profile" 
          path="/profile"
          active={location.pathname === '/profile'} 
          onClick={navigate} 
        />
      </div>
    </div>
  );
}
