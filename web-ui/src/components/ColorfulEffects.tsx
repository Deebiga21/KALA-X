import React, { useState } from 'react';
import ShaderGroupSwitcher from './ShaderGroupSwitcher';

export default function ColorfulEffects() {
    const [colorPreset, setColorPreset] = useState<'cyan' | 'violet' | 'amber' | 'green' | 'custom'>('cyan');
    const [customColor, setCustomColor] = useState('#00e5ff');

    const presets = {
        cyan: { tint: '#00e5ff', chromatic: 10, zoom: 295 },
        violet: { tint: '#8a2be2', chromatic: 20, zoom: 250 },
        amber: { tint: '#ffbf00', chromatic: 5, zoom: 320 },
        green: { tint: '#00ff7f', chromatic: 15, zoom: 300 },
        custom: { tint: customColor, chromatic: 10, zoom: 295 }
    };

    const currentConfig = presets[colorPreset];

    return (
        <div style={{ width: '100vw', height: '100vh', display: 'flex', flexDirection: 'column' }}>
            <div style={{ padding: '1rem', background: '#111', color: '#fff', display: 'flex', gap: '1rem', alignItems: 'center', zIndex: 10 }}>
                <span>Select Effect Color:</span>
                {(['cyan', 'violet', 'amber', 'green', 'custom'] as const).map(preset => (
                    <button
                        key={preset}
                        onClick={() => setColorPreset(preset)}
                        style={{
                            padding: '0.5rem 1rem',
                            background: colorPreset === preset ? '#333' : '#222',
                            color: '#fff',
                            border: '1px solid #444',
                            borderRadius: '4px',
                            cursor: 'pointer',
                            textTransform: 'capitalize'
                        }}
                    >
                        {preset}
                    </button>
                ))}
                
                {colorPreset === 'custom' && (
                    <input 
                        type="color" 
                        value={customColor} 
                        onChange={(e) => setCustomColor(e.target.value)} 
                        style={{ cursor: 'pointer' }}
                    />
                )}
            </div>

            <div style={{ flex: 1, position: 'relative' }}>
                <ShaderGroupSwitcher 
                    background="#0a0a0a"
                    tint={currentConfig.tint}
                    chromatic={currentConfig.chromatic}
                    zoom={currentConfig.zoom}
                    hover={120} // increase hover for better cursor effect
                />
            </div>
        </div>
    );
}
