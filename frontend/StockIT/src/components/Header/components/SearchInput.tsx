interface SearchInputProps {
  value: string;
  onChange: (value: string) => void;
  onFocus: () => void;
}

export function SearchInput({ value, onChange, onFocus }: SearchInputProps) {
  return (
    <div className="flex items-center bg-white/90 rounded-full px-5 h-12 gap-3">
      <svg className="w-5 h-5 text-gray-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
      </svg>
      <input
        type="text"
        value={value}
        onChange={e => onChange(e.target.value)}
        onFocus={onFocus}
        placeholder="Pesquise produtos e ambientes..."
        className="flex-1 bg-transparent outline-none text-gray-800 text-base placeholder-gray-400"
      />
    </div>
  );
}
