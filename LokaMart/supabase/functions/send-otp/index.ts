import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"
import { Resend } from "https://esm.sh/resend"

serve(async (req) => {

  try {

    const { email } = await req.json()

    // Generate OTP
    const otp = Math.floor(
      100000 + Math.random() * 900000
    ).toString()

    // Expired 5 menit
    const expiresAt = new Date(
      Date.now() + 5 * 60 * 1000
    ).toISOString()

    // Supabase client
    const supabase = createClient(
      "https://gknjnodksyrhmaxzkdrk.supabase.co",
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imdrbmpub2Rrc3lyaG1heHprZHJrIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3OTM1NDE4NywiZXhwIjoyMDk0OTMwMTg3fQ.OBkauf28Vnb6xV0DpYrOtdCYFhNGsxuQBFsGXDuPZNc"
    )

    // Simpan OTP
    const { error } = await supabase
      .from("email_otps")
      .insert({
        email: email,
        otp_code: otp,
        expires_at: expiresAt,
        is_verified: false
      })

    if (error) throw error

    // RESEND
    const resend = new Resend(
      Deno.env.get("RESEND_API_KEY")
    )

    // KIRIM EMAIL
    await resend.emails.send({
      from: "LokaMart <onboarding@resend.dev>",
      to: email,
      subject: "Kode OTP LokaMart",
      html: `
        <div style="font-family:sans-serif">
          <h2>Kode OTP LokaMart</h2>
          <p>Gunakan kode berikut untuk verifikasi akun:</p>

          <h1>${otp}</h1>

          <p>Kode berlaku selama 5 menit.</p>
        </div>
      `
    })

    return new Response(
      JSON.stringify({
        success: true,
        message: "OTP berhasil dikirim"
      }),
      {
        headers: {
          "Content-Type": "application/json"
        },
        status: 200
      }
    )

  } catch (e) {

    return new Response(
      JSON.stringify({
        success: false,
        error: e.message
      }),
      {
        headers: {
          "Content-Type": "application/json"
        },
        status: 500
      }
    )
  }
})