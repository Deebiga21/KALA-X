import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Outlet } from 'react-router-dom';
import HomeScreen from './screens/HomeScreen';
import CatalogScreen from './screens/CatalogScreen';
import InsightsScreen from './screens/InsightsScreen';
import ProfileScreen from './screens/ProfileScreen';
import BottomNavigation from './components/BottomNavigation';
import CaptureScreen from './screens/product/CaptureScreen';
import EnhancementScreen from './screens/product/EnhancementScreen';
import VoiceCatalogScreen from './screens/product/VoiceCatalogScreen';
import SmartPricingScreen from './screens/product/SmartPricingScreen';
import ReadinessScreen from './screens/product/ReadinessScreen';
import FinalListingScreen from './screens/product/FinalListingScreen';
import ShaderGroupSwitcher from './components/ShaderGroupSwitcher';

function Layout() {
  const [loaded, setLoaded] = useState(false);
  useEffect(() => setLoaded(true), []);

  return (
    <div className="flex justify-center items-center min-h-screen bg-black sm:bg-gray-900 sm:p-8">
      <div className="relative w-full h-[100dvh] sm:w-[400px] sm:h-[866px] sm:rounded-[3rem] overflow-hidden sm:border-[8px] sm:border-gray-800 shadow-2xl">
        <ShaderGroupSwitcher 
          background="#0f172a" 
          tint="#00e5ff" 
          chromatic={15} 
          hover={120} 
          zoom={250}
          style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', minWidth: 0, minHeight: 0 }}
        >
          <div className="relative h-full w-full z-10">
            <Outlet />
          </div>
          <BottomNavigation />
        </ShaderGroupSwitcher>
      </div>
    </div>
  );
}

function CreationLayout() {
  return (
    <div className="flex justify-center items-center min-h-screen bg-black sm:bg-gray-900 sm:p-8">
      <div className="relative w-full h-[100dvh] sm:w-[400px] sm:h-[866px] sm:rounded-[3rem] overflow-hidden sm:border-[8px] sm:border-gray-800 shadow-2xl bg-navy-bg">
        <Outlet />
      </div>
    </div>
  );
}

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<HomeScreen />} />
          <Route path="catalog" element={<CatalogScreen />} />
          <Route path="insights" element={<InsightsScreen />} />
          <Route path="profile" element={<ProfileScreen />} />
        </Route>
        
        <Route path="/create" element={<CreationLayout />}>
          <Route index element={<CaptureScreen />} />
          <Route path="enhance" element={<EnhancementScreen />} />
          <Route path="voice" element={<VoiceCatalogScreen />} />
          <Route path="pricing" element={<SmartPricingScreen />} />
          <Route path="readiness" element={<ReadinessScreen />} />
          <Route path="final" element={<FinalListingScreen />} />
        </Route>
      </Routes>
    </Router>
  );
}
