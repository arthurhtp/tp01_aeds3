import { AmbienteCard } from "../../AmbienteCard";
import type { CardListProps } from "../types";
import { CARD_WIDTH, VISIBLE_CARDS } from "../constants";

export function CardList({
  ambientes,
  index,
  onEditar,
  onExcluir,
}: CardListProps) {
  return (
    <div
      className="overflow-hidden rounded-2xl mx-12"
      style={{ width: `${VISIBLE_CARDS * CARD_WIDTH - 20}px` }}
    >
      <div
        className="flex gap-5 pt-3 pb-28 transition-transform duration-300"
        style={{ transform: `translateX(-${index * CARD_WIDTH}px)` }}
      >
        {ambientes.map((amb) => (
          <AmbienteCard
            key={amb.id}
            ambiente={amb}
            onEditar={onEditar}
            onExcluir={onExcluir}
          />
        ))}
      </div>
    </div>
  );
}
