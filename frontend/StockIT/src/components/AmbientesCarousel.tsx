import { useState, useRef } from 'react';
import type { Ambiente } from '../types/home.types';
import AmbienteCard from './AmbienteCard';

interface AmbientesCarouselProps {
  ambientes: Ambiente[];
  onEditar: (id: number) => void;
  onExcluir: (id: number) => void;
}

const CARD_WIDTH = 240 + 20; // width + gap

export default function AmbientesCarousel({ ambientes, onEditar, onExcluir }: AmbientesCarouselProps) {
  const [index, setIndex] = useState(0);
  const VISIBLE = 3;
  const max = Math.max(0, ambientes.length - VISIBLE);

  function prev() { setIndex(i => Math.max(0, i - 1)); }
  function next() { setIndex(i => Math.min(max, i + 1)); }

  return (
    <div className="relative flex justify-center">
      {/* Prev Button */}
      <button
        onClick={prev}
        disabled={index === 0}
        className="absolute left-0 top-1/2 -translate-y-1/2 z-10 w-11 h-11 rounded-full bg-white shadow-md flex items-center justify-center text-emerald-600 hover:scale-110 hover:shadow-lg transition-all disabled:opacity-40 disabled:cursor-not-allowed disabled:scale-100"
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        </svg>
      </button>

      {/* Wrapper */}
      <div className="overflow-hidden rounded-2xl mx-12" style={{ width: `${VISIBLE * CARD_WIDTH - 20}px` }}>
        <div
          className="flex gap-5 py-3 transition-transform duration-300"
          style={{ transform: `translateX(-${index * CARD_WIDTH}px)` }}
        >
          {ambientes.map(amb => (
            <AmbienteCard
              key={amb.id}
              ambiente={amb}
              onEditar={onEditar}
              onExcluir={onExcluir}
            />
          ))}
        </div>
      </div>

      {/* Next Button */}
      <button
        onClick={next}
        disabled={index >= max}
        className="absolute right-0 top-1/2 -translate-y-1/2 z-10 w-11 h-11 rounded-full bg-white shadow-md flex items-center justify-center text-emerald-600 hover:scale-110 hover:shadow-lg transition-all disabled:opacity-40 disabled:cursor-not-allowed disabled:scale-100"
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>
  );
}
