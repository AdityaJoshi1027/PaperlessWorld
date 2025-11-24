import React from 'react';

interface LogoProps {
  size?: number;
  className?: string;
}

const Logo: React.FC<LogoProps> = ({ size = 32, className = '' }) => {
  return (
    <svg 
      xmlns="http://www.w3.org/2000/svg" 
      viewBox="0 0 128 128" 
      width={size} 
      height={size}
      className={className}
      style={{ display: 'inline-block', verticalAlign: 'middle' }}
    >
      <defs>
        <linearGradient id="bgGradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style={{ stopColor: '#3b82f6', stopOpacity: 1 }} />
          <stop offset="100%" style={{ stopColor: '#1e40af', stopOpacity: 1 }} />
        </linearGradient>
        <linearGradient id="docGradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style={{ stopColor: '#ffffff', stopOpacity: 1 }} />
          <stop offset="100%" style={{ stopColor: '#e0e7ff', stopOpacity: 1 }} />
        </linearGradient>
      </defs>
      
      <circle cx="64" cy="64" r="60" fill="url(#bgGradient)" />
      
      <rect x="42" y="36" width="38" height="50" rx="3" fill="#cbd5e1" opacity="0.6" />
      <rect x="38" y="40" width="38" height="50" rx="3" fill="#e2e8f0" opacity="0.8" />
      <rect x="34" y="44" width="38" height="50" rx="3" fill="url(#docGradient)" stroke="#3b82f6" strokeWidth="1.5" />
      
      <line x1="40" y1="54" x2="66" y2="54" stroke="#3b82f6" strokeWidth="2" strokeLinecap="round" />
      <line x1="40" y1="62" x2="62" y2="62" stroke="#60a5fa" strokeWidth="2" strokeLinecap="round" />
      <line x1="40" y1="70" x2="64" y2="70" stroke="#60a5fa" strokeWidth="2" strokeLinecap="round" />
      <line x1="40" y1="78" x2="58" y2="78" stroke="#93c5fd" strokeWidth="2" strokeLinecap="round" />
      
      <circle cx="76" cy="52" r="2.5" fill="#fbbf24" />
      <circle cx="84" cy="52" r="2.5" fill="#fbbf24" />
      <circle cx="92" cy="52" r="2.5" fill="#fbbf24" />
      
      <circle cx="76" cy="62" r="2.5" fill="#f59e0b" />
      <circle cx="84" cy="62" r="2.5" fill="#fbbf24" />
      <circle cx="92" cy="62" r="2.5" fill="#f59e0b" />
      
      <circle cx="76" cy="72" r="2.5" fill="#fbbf24" />
      <circle cx="84" cy="72" r="2.5" fill="#f59e0b" />
      <circle cx="92" cy="72" r="2.5" fill="#fbbf24" />
      
      <circle cx="64" cy="100" r="12" fill="none" stroke="#fbbf24" strokeWidth="2" />
      <ellipse cx="64" cy="100" rx="5" ry="12" fill="none" stroke="#fbbf24" strokeWidth="1.5" />
      <line x1="52" y1="100" x2="76" y2="100" stroke="#fbbf24" strokeWidth="1.5" />
      <path d="M 64 88 Q 70 94 64 100 Q 58 106 64 112" fill="none" stroke="#fbbf24" strokeWidth="1.5" />
    </svg>
  );
};

export default Logo;
