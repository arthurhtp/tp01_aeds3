import { useState, useEffect } from 'react';
import type { Ambiente } from '../../../types/home.types';
import { ambientesService } from '../../../services/home.service';

export function useAmbientes() {
  const [ambientes, setAmbientes] = useState<Ambiente[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  async function carregarAmbientes() {
    try {
      const data = await ambientesService.getAll();
      setAmbientes(data);
      setError(false);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    carregarAmbientes();
  }, []);

  return {
    ambientes,
    setAmbientes,
    loading,
    error,
    carregarAmbientes,
  };
}
