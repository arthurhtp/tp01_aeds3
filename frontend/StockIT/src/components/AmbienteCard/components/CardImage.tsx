import type { CardImageProps } from "../types";
import { getImgAmbiente, getNomeAmbiente } from "../utils";

export function CardImage({ ambiente }: CardImageProps) {
  return (
    <div className="h-40 w-full overflow-hidden rounded-t-2xl bg-gradient-to-br from-gray-50 to-gray-100 border-b-2 border-dashed border-gray-200 group-hover:border-emerald-400 transition-colors flex items-center justify-center">
      <img
        src={getImgAmbiente(ambiente.tipo)}
        alt={getNomeAmbiente(ambiente)}
        className="w-full h-full object-cover"
      />
    </div>
  );
}
