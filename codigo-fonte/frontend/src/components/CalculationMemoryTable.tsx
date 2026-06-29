import { Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import type { CalculoMemoria } from '../services/api';
import { formatMoney } from '../utils/money';

export default function CalculationMemoryTable({ memoria }: { memoria: CalculoMemoria[] }) {
  return (
    <Table size="small">
      <TableHead>
        <TableRow>
          <TableCell>Competência</TableCell>
          <TableCell>Índice</TableCell>
          <TableCell>Valor anterior</TableCell>
          <TableCell>Valor corrigido</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {memoria.map((item) => (
          <TableRow key={`${item.competencia}-${item.indicePercentual}`}>
            <TableCell>{item.competencia}</TableCell>
            <TableCell>{item.indicePercentual.toFixed(2)}%</TableCell>
            <TableCell>{formatMoney(item.valorAnterior)}</TableCell>
            <TableCell>
              <Typography fontWeight={700}>{formatMoney(item.valorCorrigido)}</Typography>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}