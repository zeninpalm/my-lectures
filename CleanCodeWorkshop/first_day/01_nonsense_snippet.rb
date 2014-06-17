def ss(aa)
  rr = 0
  uu = Math::sqrt(aa).floor
  if aa == 2 or aa == 1
    return True
  end

  1.upto(uu) do |i|
    if aa % i == 0
      return False
    end
  end

  return True
end
