import { Navigate, Route, Routes } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import DashboardPage from '../pages/DashboardPage';
import AtualizacaoMonetariaPage from '../pages/AtualizacaoMonetariaPage';
import CalculoReversoPage from '../pages/CalculoReversoPage';
import IndicesPage from '../pages/IndicesPage';
import HistoricoPage from '../pages/HistoricoPage';

import ConsolidacaoPage from '../pages/ConsolidacaoPage';
import ConfiguracoesSistemaPage from '../pages/ConfiguracoesSistemaPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/atualizacao" element={<AtualizacaoMonetariaPage />} />
        <Route path="/reverso" element={<CalculoReversoPage />} />
        <Route path="/indices" element={<IndicesPage />} />
        <Route path="/historico" element={<HistoricoPage />} />
        <Route path="/consolidacao" element={<ConsolidacaoPage />} />
        <Route path="/configuracoes" element={<ConfiguracoesSistemaPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}