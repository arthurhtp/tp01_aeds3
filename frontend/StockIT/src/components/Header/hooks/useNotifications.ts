import { useState, useEffect } from 'react';
import type { Notificacao, Ambiente } from '../../../types/home.types';
import { ambientesService } from '../../../services/home.service';

const DIAS_LIMITE = 3;

function getNomeAmbiente(amb: Ambiente): string {
  return amb.nome ?? amb.nomeAmbiente ?? 'Ambiente';
}

export function useNotifications() {
  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([]);
  const [showNotif, setShowNotif] = useState(false);

  useEffect(() => {
    async function carregarNotificacoes() {
      try {
        const ambientes = await ambientesService.getAll();
        const hoje = new Date();
        const vencendo: Notificacao[] = [];

        ambientes.forEach((amb: Ambiente) => {
          amb.itens?.forEach(item => {
            const vencimento = new Date(item.vencimento);
            const diffDias = (vencimento.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24);
            if (diffDias <= DIAS_LIMITE) {
              vencendo.push({
                texto: `Produto (${item.quantidade} un) - ${getNomeAmbiente(amb)}`,
                ambienteId: amb.id,
                alimentoId: item.alimentoId,
              });
            }
          });
        });

        setNotificacoes(vencendo);
      } catch (err) {
        console.error('Erro ao carregar notificações:', err);
      }
    }

    carregarNotificacoes();
  }, []);

  return { notificacoes, showNotif, setShowNotif, DIAS_LIMITE };
}
