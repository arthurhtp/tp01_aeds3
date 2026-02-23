interface LogoProps {}

export function Logo({}: LogoProps) {
  return (
    <div className="flex items-center gap-3 w-48 shrink-0">
      <h1 className="text-2xl font-bold text-white">StockIT</h1>
    </div>
  );
}
