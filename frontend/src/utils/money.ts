export function formatMoney(value: number | string | null | undefined) {
  const numericValue = typeof value === 'string' ? Number(value) : value ?? 0;
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(Number.isFinite(numericValue) ? numericValue : 0);
}

export function parseMoney(value: string) {
  const normalized = value.replace(/\./g, '').replace(',', '.').replace(/[^0-9.-]/g, '');
  const parsed = Number(normalized);
  return Number.isFinite(parsed) ? parsed : 0;
}