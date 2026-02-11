export interface Beneficio {
  id: number;
  nome: string;
  descricao: string;
  valor: number;
  ativo: boolean;
  version: number;
}

export interface BeneficioRequest {
  nome: string;
  descricao?: string | null;
  valor: number;
  ativo: boolean;
}

export interface TransferRequest {
  fromId: number | null;
  toId: number | null;
  valor: number | null;
}