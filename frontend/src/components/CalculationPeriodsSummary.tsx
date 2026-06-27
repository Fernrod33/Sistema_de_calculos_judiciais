import { Box, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import type { CalculoPeriodo } from '../services/api';

type Props = {
  periodos: CalculoPeriodo[];
};

export default function CalculationPeriodsSummary({ periodos }: Props) {
  return (
    <Box>
      <Typography variant="subtitle1" fontWeight={800} mb={1}>Períodos aplicados</Typography>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Ordem</TableCell>
            <TableCell>Data inicial</TableCell>
            <TableCell>Data final</TableCell>
            <TableCell>Índice</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {periodos.map((periodo) => (
            <TableRow key={`${periodo.ordem}-${periodo.dataInicial}-${periodo.dataFinal}`}>
              <TableCell>{periodo.ordem}</TableCell>
              <TableCell>{periodo.dataInicial}</TableCell>
              <TableCell>{periodo.dataFinal}</TableCell>
              <TableCell>{periodo.indiceUtilizado}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Box>
  );
}