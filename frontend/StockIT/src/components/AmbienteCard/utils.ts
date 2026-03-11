import type { Ambiente } from "../../types/home.types";
import imgD from "../../assets/images/ambiente-images/D.png";
import imgF from "../../assets/images/ambiente-images/F.png";
import imgG from "../../assets/images/ambiente-images/G.png";

export function getNomeAmbiente(amb: Ambiente): string {
  return amb.nome ?? amb.nomeAmbiente ?? "Ambiente";
}

export function getImgAmbiente(tipo: number): string {
  if (tipo === 1) return imgG;
  if (tipo === 2) return imgF;
  if (tipo === 3) return imgD;
  return imgD;
}
