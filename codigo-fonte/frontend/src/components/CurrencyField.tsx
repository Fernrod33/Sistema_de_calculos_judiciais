import { TextField, type TextFieldProps } from '@mui/material';

type CurrencyFieldProps = Omit<TextFieldProps, 'value' | 'onChange'> & {
  value: string;
  onChange: (value: string) => void;
};

export default function CurrencyField({ value, onChange, ...props }: CurrencyFieldProps) {
  return (
    <TextField
      {...props}
      value={value}
      onChange={(event) => onChange(event.target.value)}
      inputProps={{ inputMode: 'decimal' }}
      placeholder="0,00"
    />
  );
}