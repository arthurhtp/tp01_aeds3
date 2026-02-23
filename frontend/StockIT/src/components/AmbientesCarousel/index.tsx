import { useState } from 'react';
import type { AmbientesCarouselProps } from './types';
import { NavigationButton, CardList } from './components';
import { VISIBLE_CARDS } from './constants';

export function AmbientesCarousel({
  ambientes,
  onEditar,
  onExcluir,
}: AmbientesCarouselProps) {
  const [index, setIndex] = useState(0);
  const max = Math.max(0, ambientes.length - VISIBLE_CARDS);

  function prev() {
    setIndex(i => Math.max(0, i - 1));
  }

  function next() {
    setIndex(i => Math.min(max, i + 1));
  }

  return (
    <div className="relative flex justify-center">
      <NavigationButton disabled={index === 0} onClick={prev} direction="prev" />
      <CardList ambientes={ambientes} index={index} onEditar={onEditar} onExcluir={onExcluir} />
      <NavigationButton disabled={index >= max} onClick={next} direction="next" />
    </div>
  );
}
