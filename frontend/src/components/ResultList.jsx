export default function ResultList({ items }) {
  return (
    <dl className="divide-y divide-line rounded-lg border border-line text-sm">
      {items.map(([label, value]) => (
        <div key={label} className="flex justify-between gap-4 px-3 py-2">
          <dt className="text-muted-fg">{label}</dt>
          <dd className="text-right font-medium">{value}</dd>
        </div>
      ))}
    </dl>
  );
}
