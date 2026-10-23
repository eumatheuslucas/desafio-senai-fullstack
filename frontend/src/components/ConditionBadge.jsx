import { CONDITION_LABELS } from '../lib/constants.js';

const STYLES = {
  APTO: 'border-success-border bg-success-soft text-success-text',
  MAL_CONDICIONADO: 'border-warning-border bg-warning-soft text-warning-text',
  DOENTE: 'border-danger-border bg-danger-soft text-danger-text',
};

export default function ConditionBadge({ condition }) {
  return (
    <span className={`inline-flex items-center gap-1.5 rounded-lg border px-2 py-0.5 text-xs font-medium ${STYLES[condition]}`}>
      <span aria-hidden="true" className="h-1.5 w-1.5 rounded-full bg-current" />
      {CONDITION_LABELS[condition]}
    </span>
  );
}
